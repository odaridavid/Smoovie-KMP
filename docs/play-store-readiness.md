# Play Store Readiness

Tracking what's needed before Smoovie ships to the Google Play Store. Last updated 2026-05-12.

## Blockers (Play Console will reject or it bites on day one)

### 1. Release signing config — DONE (gradle wiring)
`composeApp/build.gradle.kts` declares a `release` signing config that pulls credentials from `local.properties` or env vars. Debug builds work without setup; release builds fail loudly if credentials are absent.

To enable release signing on a machine, add to `local.properties` (gitignored):
```
release.store.file=/absolute/path/to/upload.keystore
release.store.password=<store password>
release.key.alias=smoovie
release.key.password=<key password>
```
Or for CI: `RELEASE_STORE_FILE`, `RELEASE_STORE_PASSWORD`, `RELEASE_KEY_ALIAS`, `RELEASE_KEY_PASSWORD`.

Still to do (one-time, by the developer — not committed to repo):
- Generate the upload keystore: `keytool -genkey -v -keystore upload.keystore -alias smoovie -keyalg RSA -keysize 2048 -validity 10000`. Store outside the repo (e.g. `~/.android/smoovie-upload.keystore`).
- Verify with `./gradlew :composeApp:bundleRelease` after credentials are in place.
- Enroll the app in **Play App Signing** so Google holds the actual app signing key and you only manage the upload key.

### 2. R8 / minification + ProGuard rules — DONE
- `isMinifyEnabled = true` and `isShrinkResources = true` in the release build type.
- `composeApp/proguard-rules.pro` with keep rules for kotlinx.serialization, Ktor, Room, and Coil.
- **Verify**: run `./gradlew :composeApp:bundleRelease` with signing configured and smoke-test navigation, networking, watchlist persistence, and image loading on a real device.

### 3. Backup rules — DONE
Manifest references explicit `dataExtractionRules` (Android 12+) and `fullBackupContent` (older). Default behaviour includes the Room watchlist DB so users don't lose their list on reinstall.

### 4. Privacy policy URL — PENDING (developer action)
Required field on the Play listing even for apps that don't collect personal data. Host one (GitHub Pages on the `smoovie` repo is fine) and link it in the Play Console listing.

### 5. Data safety form — PENDING (Play Console)
Fill in the form in Play Console. Honest disclosure required:
- Firebase App Check transmits device attestation data.
- Crashlytics collects crash logs and device info.
- TMDB requests go via a Cloud Function proxy (IP/User-Agent reach the proxy).
- Watchlist data is stored locally in Room — no transmission.

### 6. Content rating — PENDING (Play Console)
Complete the IARC questionnaire in Play Console. Movie metadata + posters → likely Teen; let the questionnaire decide.

### 7. TMDB attribution — DONE
Settings screen shows the TMDB logo and required attribution text per TMDB API ToS §3.

### 8. TMDB token security — DONE
Token never ships in the binary. All TMDB requests route through a Firebase Cloud Function (`europe-west1`) that holds the token in Secret Manager. App Check enforces that only genuine app builds can call the proxy.

## Strong recommendations

### 9. Crash reporting — DONE
Firebase Crashlytics wired on both Android and iOS. Collection disabled in debug builds. dSYM upload run script added to the Xcode target for symbolicated iOS crash reports.

### 10. Material3 is on an alpha — not actionable
`org.jetbrains.compose.material3:material3` has no stable release; the JetBrains KMP wrapper is alpha-only. `1.11.0-alpha07` is the latest available. Monitor [Compose Multiplatform releases](https://github.com/JetBrains/compose-multiplatform/releases) and upgrade when a stable is published.

### 11. versionCode / versionName strategy — DONE
`version.properties` at repo root holds `versionCode` and `versionName`. `build.gradle.kts` reads from it at build time. Bump both values and commit before each release.

### 12. In-app review prompt — DONE
Play In-App Review API (Android) and `SKStoreReviewController` (iOS) triggered when the user adds their 3rd watchlist item. The OS rate-limits the dialog to a maximum of 3 times per year.

### 13. Release CI workflow — SKIPPED FOR NOW
Add a workflow under `.github/workflows/` that runs `bundleRelease` on tag push and uploads to the internal track via `r0adkll/upload-google-play-action`. Prerequisites: upload keystore generated (§1) and GitHub secrets configured. See previous doc version for the full workflow snippet.

### 14. Per-app language config — not applicable yet
Only English supported. If localizations are added, add `composeApp/src/androidMain/res/xml/locales_config.xml` and reference it from the manifest for Android 13+ per-app language settings.

## Listing-side (not code, but blocks upload)

- [ ] High-res icon (512×512 PNG)
- [ ] Feature graphic (1024×500 PNG)
- [ ] Phone screenshots (≥2)
- [ ] Short description (≤80 chars) + full description (≤4000 chars)
- [ ] Category, contact email
- [ ] Target audience and ads declarations

## Rollout

- Don't ship straight to production. Use **Internal testing → Closed testing → Open testing → Production**.
- Personal Play developer accounts created after Nov 2023 require **12 testers running the app for 14 days** on closed testing before the production track unlocks. Plan that into the timeline.