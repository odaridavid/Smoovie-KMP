# Releasing Smoovie

How the release pipeline works, how to cut a release, and the gotchas that bite at release time. For local dev setup, see `onboarding.md`.

## Where things live

| Thing | Location | Notes |
|---|---|---|
| Upload keystore | Maintainer's private storage (1Password + encrypted backup) | Never in the repo. Base64-encoded into a GitHub secret for CI. |
| GitHub release secrets | Repo → Settings → Secrets and variables → Actions | Five secrets, listed below. |
| Play service account | Play Console → Users and permissions; JSON key held by maintainer | Granted Release manager on the app. |
| TMDB API token | Google Secret Manager, `smoovie-kmp` project, secret name `TMDB_TOKEN` | Cloud Function reads at runtime. Never touches the device. |
| Cloud Function source | `functions/src/index.ts` | TypeScript. Deploys to `europe-west1`. |
| Privacy policy | https://smoovie-kmp.web.app/privacy | Linked from the Play listing. |
| Version | `version.properties` at repo root | Bump and commit before each release. |
| Listing copy + Data Safety | `docs/play-store-listing.md` | Source of truth — update in the same PR as behaviour changes. |

## Android release pipeline

Triggered manually from GitHub Actions. Workflow lives in `.github/workflows/release-internal.yml`.

```
GitHub Actions ──► restore keystore from secret
              ──► ./gradlew :composeApp:bundleRelease  (signs the AAB)
              ──► upload AAB to Play Console internal track as DRAFT
              ──► Crashlytics mapping uploaded automatically during build
```

The upload is **draft**, never `completed`. Always review in Play Console before promoting to testers. Change `status:` in the workflow once you trust the pipeline.

### Required GitHub secrets

| Secret | Source |
|---|---|
| `RELEASE_KEYSTORE_BASE64` | `base64 -i ~/.android/smoovie-upload.keystore \| pbcopy` |
| `RELEASE_STORE_PASSWORD` | Same as `local.properties`. |
| `RELEASE_KEY_ALIAS` | Same. |
| `RELEASE_KEY_PASSWORD` | Same. |
| `PLAY_SERVICE_ACCOUNT_JSON` | JSON key for a Google Cloud service account granted **Release manager** for the app via Play Console → Users and permissions. |

Rotate any of these by overwriting the secret value — no workflow change needed.

### Cutting a release

1. **Bump** `versionName` in `version.properties` (semver, e.g. `1.0.1`). `versionCode` is auto-bumped by CI
2. Commit and push.
3. **GitHub Actions** → **Release to Internal Testing** → **Run workflow** → optional release notes → **Run**.
4. Wait 3-5 minutes.
5. **Play Console** → Smoovie → **Test and release** → **Internal testing** → review the draft → **Start rollout to Internal testing**.

### Versioning

- `versionName` — semver (`MAJOR.MINOR.PATCH`). Patch for fixes, minor for features. Human-controlled, lives in `version.properties`.
- `versionCode` — auto-derived by the workflow as `100 + GITHUB_RUN_NUMBER`. Monotonically increases per workflow run; survives until the workflow file is renamed or the repo is migrated.

The 100 offset gives headroom above the manual versionCodes (1, 2) used during initial setup. The `version.properties` `versionCode` entry is only read for local release-builds; CI overrides it via `-PversionCodeOverride=…`.

If you ever need to manually upload a release outside the workflow (emergency hotfix, sideloaded testing), pick a `versionCode` higher than the latest CI-produced one in Play Console to avoid the "version already used" rejection.

### Tagging

After a successful AAB upload, the workflow creates an annotated git tag (`v{versionName}-{versionCode}`, e.g. `v1.0.1-103`) on the release commit and publishes a matching GitHub Release with the release-notes input as its body. Both are pushed to the remote.

If the upload step fails, no tag is created — tag presence reflects a real, accepted Play upload. Tags are immutable; rolling back a release means cutting a new one with a higher versionCode, not retagging.

The job needs `contents: write` permission (set at the job level) so the runner's `GITHUB_TOKEN` can push the tag.

### Release notes ("What's new")

The workflow_dispatch form has a **Release notes** input. Whatever you type there gets written to `whatsnew/whatsnew-en-US` and uploaded as the **"What's new in this version"** text on the Play listing.

- Default fallback: `Internal testing release.` — unfilled runs still produce a valid file.
- Play caps the text at **500 characters per locale**; anything longer is truncated server-side.
- For multi-locale notes, add more `whatsnew-<locale>` files in the same step (e.g. `whatsnew-de-DE`). The upload action picks up every locale file in the directory.
- The field is per-run — it doesn't persist between releases.

For richer per-version notes (changelog from commits since the last tag), wire `release_notes` from `git log` output instead of the manual input. Worth it once you're releasing more than weekly.

### Promotion path

```
Internal testing  ──►  Closed testing  ──►  Open testing  ──►  Production
   (any tester)        (allowlist)         (public link)        (Play Store)
```

This developer account predates the Nov 2023 personal-account rule, so the "12 testers / 14 days closed testing" gate does not apply — internal → production direct is allowed. Use intermediate tracks for graduated rollout if you want to catch regressions before the public sees them.

## iOS release process

Not automated. Manual archive-and-upload.

1. Bump `MARKETING_VERSION` and `CURRENT_PROJECT_VERSION` in `iosApp/iosApp.xcodeproj` (or via Xcode's General target settings).
2. Xcode → **iosApp** scheme → destination **Any iOS Device (arm64)**.
3. **Product → Archive**. Crashlytics dSYM upload runs as a build phase.
4. Organizer → select archive → **Distribute App** → **App Store Connect** → **Upload**.
5. App Store Connect → TestFlight → wait for processing (10-30 min) → add to internal/external testing group.
6. Promote to App Store review when ready.

App Check on iOS uses **App Attest** in release. Requires real hardware — simulators won't produce a valid attestation.

## Operations

The Cloud Function (`tmdbproxy`) is in the app's critical path. If it goes down or the TMDB token gets revoked, every install fails to load content.

| Concern | Setup |
|---|---|
| Region | `europe-west1` |
| Memory / timeout | 256 MiB, 30 s |
| Secret (`TMDB_TOKEN`) | Google Secret Manager. Rotate via `firebase functions:secrets:set TMDB_TOKEN`. |
| Budget alert | $5/month, 50/90/100% email alerts. |
| Uptime check | Periodic ping against `/3/configuration`. |
| Logs | `firebase functions:log` or Cloud Logging UI. Function tags: `tmdbproxy`. |

Still open:
- **Secret rotation calendar reminder.** TMDB tokens don't auto-expire, but a 12-month reminder forces a check that secret access still works.
- **Log-based alert on upstream 401s.** Cloud Logging metric matching `jsonPayload.message="TMDB upstream non-2xx" AND jsonPayload.status=401` would catch a revoked TMDB token before users do.

## Gotchas that bite at release time

### Play App Signing + App Check fingerprint

When you enroll in **Play App Signing**, Google generates a separate app signing key and re-signs every distributed APK with it. Your upload key only proves you uploaded the AAB.

On-device, Play Integrity attestations are signed against the **app signing key**, not the upload key. Firebase rejects the App Check token unless the app signing key's SHA-256 is registered against the Firebase project.

Both fingerprints must be in Firebase Console (Project Settings → Your Android app → SHA certificate fingerprints):

- **Upload key SHA-256** — `keytool -list -v -keystore ~/.android/smoovie-upload.keystore` on your machine.
- **App signing key SHA-256** — Play Console → Setup → **App integrity** → **App signing** tab → "App signing key certificate".

**Symptom if missing:** Movies/Shows screens show "Failed to load." The function returns 401 "Missing App Check token" because Firebase silently fails to mint a token, and the Ktor `AppCheckHeader` plugin skips attaching the header when the token is null.

**Diagnostic:** `AndroidAppCheckTokenProvider` logs the underlying `FirebaseAppCheckException` via `android.util.Log.e("AppCheck", ...)`. Filter logcat with `-s AppCheck:E` to see the actual error.

### Sideloaded release builds don't pass App Check

`adb install`ing a release-signed APK makes Play Integrity return `UNRECOGNIZED_VERSION` instead of `PLAY_RECOGNIZED`. Firebase refuses to mint a token. Movies don't load.

The only reliable way to test a release build end-to-end is **install via Play Store internal testing**. No local workaround.

## Listing copy + Data Safety

All Play Console paperwork (short description, full description, Data Safety wizard answers, reviewer notes) lives in `docs/play-store-listing.md`. Update there first, then paste into Play Console — keeps the source of truth in git.

If app behaviour changes that affects data flow (new SDK, new collection point, new analytics tool), update `play-store-listing.md` in the same PR as the behaviour change. The Play Data Safety disclosure has to match reality.
