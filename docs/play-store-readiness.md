# Play Store Readiness

Tracking what's needed before Smoovie ships to the Google Play Store. Snapshot taken 2026-05-08.

## Blockers (Play Console will reject or it bites on day one)

### 1. Release signing config — DONE (gradle wiring)
`composeApp/build.gradle.kts` now declares a `release` signing config that pulls credentials from either `local.properties` or env vars. When credentials are absent the block is skipped, so `assembleDebug` keeps working without setup. Release builds will fail loudly if the keystore isn't configured, which is the right behavior.

To enable release signing on a machine, add to `local.properties` (gitignored):
```
release.store.file=/absolute/path/to/upload.keystore
release.store.password=<store password>
release.key.alias=smoovie
release.key.password=<key password>
```
Or for CI, set the equivalent env vars: `RELEASE_STORE_FILE`, `RELEASE_STORE_PASSWORD`, `RELEASE_KEY_ALIAS`, `RELEASE_KEY_PASSWORD`.

Still to do (one-time, by the developer — not committed to repo):
- Generate the upload keystore: `keytool -genkey -v -keystore upload.keystore -alias smoovie -keyalg RSA -keysize 2048 -validity 10000`. Store outside the repo (e.g. `~/.android/smoovie-upload.keystore`).
- Verify with `./gradlew :composeApp:bundleRelease` after credentials are in place.
- Enroll the app in **Play App Signing** so Google holds the actual app signing key and you only manage the upload key.

### 2. R8 / minification + ProGuard rules — DONE (quick win)
- `isMinifyEnabled = true` and `isShrinkResources = true` in the release build type.
- `composeApp/proguard-rules.pro` with keep rules for kotlinx.serialization (the `@Serializable` nav routes in `Screen.kt` will be erased without this), Ktor, Room, and Coil.
- **Verify**: run `./gradlew :composeApp:bundleRelease` with the rules in place and smoke-test navigation, networking, watchlist persistence, and image loading on the release build.

### 3. Backup rules — DONE (quick win)
- Manifest now references explicit `dataExtractionRules` (Android 12+) and `fullBackupContent` (older). Default behavior includes the Room watchlist DB so users don't lose their list on reinstall. Tweak the XML files if that's not what you want.

### 4. Privacy policy URL
Required field on the Play listing even for apps that don't collect personal data. Host one (GitHub Pages on the `smoovie` repo is fine) and link it in the Play Console listing.

### 5. Data safety form
Honest disclosure required:
- TMDB requests transmit IP address and User-Agent to a third party.
- Watchlist data is stored locally in Room (no transmission).

### 6. Content rating
Complete the IARC questionnaire in the Play Console. Movie metadata + posters → likely Teen-ish; let the questionnaire decide.

### 7. TMDB attribution - DONE
TMDB's API terms require visible attribution: "This product uses the TMDB API but is not endorsed or certified by TMDB" plus the TMDB logo. There is no Settings/About screen yet — add one (or surface attribution at the bottom of an existing screen) before shipping.

## Strong recommendations

### 8. TMDB token security
`buildConfigField("String", "TMDB_ACCESS_TOKEN", ...)` in `composeApp/build.gradle.kts` bakes the bearer token into the APK. R8 makes it slightly harder to extract but it's still recoverable with `apktool`. Options, in order of preference:
1. Thin proxy in front of TMDB (Cloudflare Worker / Vercel function) holding the token; the app calls the proxy.
2. Move to short-lived per-user tokens if TMDB exposes them (it doesn't currently).
3. Accept the leak risk and monitor TMDB usage for abuse.

### 9. Crash reporting
Napier (already in the project) is for logs, not prod crashes. Add Crashlytics or Sentry before staged rollout — you'll want signal on day-one launches.

### 10. Material3 is on an alpha — not actionable
`org.jetbrains.compose.material3:material3` has no stable release; the JetBrains KMP wrapper is alpha-only. `1.11.0-alpha07` is already the latest available version. Monitor the [Compose Multiplatform releases](https://github.com/JetBrains/compose-multiplatform/releases) and upgrade when a stable is published.

### 11. versionCode / versionName strategy
Currently hardcoded `versionCode = 1, versionName = "1.0"`. Before the first release, set up either:
- A `version.properties` file the gradle script reads, bumped per release, or
- CI auto-increment based on tag.

Either works — pick one before you need to ship a hotfix.

### 12. Release CI workflow
`.github/workflows/` exists. Add a workflow that runs `bundleRelease` on tag push and uploads to the internal track via `r0adkll/upload-google-play-action` or `gradle-play-publisher`.

**Signing in CI (without committing the keystore):** the gradle config in §1 already reads env vars as a fallback, so the workflow just needs to decode the keystore at runtime and set the credentials.

One-time setup, locally:
```sh
base64 -i ~/.android/smoovie-upload.keystore | pbcopy   # macOS
# or: base64 -w0 ~/.android/smoovie-upload.keystore     # Linux
```

In **GitHub → Settings → Secrets and variables → Actions**, add:
- `RELEASE_KEYSTORE_BASE64` — paste the base64 output
- `RELEASE_STORE_PASSWORD`
- `RELEASE_KEY_ALIAS` (e.g. `smoovie`)
- `RELEASE_KEY_PASSWORD`
- `TMDB_ACCESS_TOKEN` — once §8's env fallback is wired (see note below)
- `PLAY_SERVICE_ACCOUNT_JSON` — base64 of the Play service-account JSON for `r0adkll/upload-google-play-action`

In the workflow:
```yaml
- name: Decode upload keystore
  env:
    KEYSTORE_BASE64: ${{ secrets.RELEASE_KEYSTORE_BASE64 }}
  run: |
    echo "$KEYSTORE_BASE64" | base64 --decode > "$RUNNER_TEMP/release.keystore"
    echo "RELEASE_STORE_FILE=$RUNNER_TEMP/release.keystore" >> "$GITHUB_ENV"

- name: Build release bundle
  env:
    RELEASE_STORE_PASSWORD: ${{ secrets.RELEASE_STORE_PASSWORD }}
    RELEASE_KEY_ALIAS: ${{ secrets.RELEASE_KEY_ALIAS }}
    RELEASE_KEY_PASSWORD: ${{ secrets.RELEASE_KEY_PASSWORD }}
    TMDB_ACCESS_TOKEN: ${{ secrets.TMDB_ACCESS_TOKEN }}
  run: ./gradlew :composeApp:bundleRelease
```

Notes:
- `$RUNNER_TEMP` is wiped between jobs and is not part of the workspace artifact, keeping the decoded keystore out of uploaded build artifacts. Never `cat` the decoded file — GitHub masks the base64 string in logs but it can't mask binary output.
- **Gap to close before this CI works**: §8 — `build.gradle.kts` currently reads `tmdb.access.token` only from `local.properties`. Add a `?: System.getenv("TMDB_ACCESS_TOKEN")` fallback so CI can inject it the same way as the signing creds.

### 13. Per-app language config
If you ever add localizations beyond English, add `composeApp/src/androidMain/res/xml/locales_config.xml` and reference it from the manifest for Android 13+ per-app language settings.

## Listing-side (not code, but blocks upload)

- High-res icon (512×512 PNG).
- Feature graphic (1024×500 PNG).
- Phone screenshots (≥2). Tablet screenshots if the app supports tablet layouts.
- Short description (≤80 chars), full description (≤4000 chars).
- Category, contact email, website (optional).
- Target audience and ads declarations.

## Rollout

- Don't ship straight to production. Use **Internal testing → Closed testing → Open testing → Production**.
- Personal Play developer accounts created after Nov 2023 require **12 testers running the app for 14 days** on closed testing before the production track unlocks. Plan that into the timeline.
