# Play Store Readiness

Tracking what's needed before Smoovie ships to the Google Play Store. Snapshot taken 2026-05-08.

## Blockers (Play Console will reject or it bites on day one)

### 1. Release signing config
Today `composeApp/build.gradle.kts` has no `signingConfigs` block. An unsigned (or debug-signed) AAB cannot be uploaded.

- Generate an upload keystore (`keytool -genkey -v -keystore upload.keystore -alias smoovie -keyalg RSA -keysize 2048 -validity 10000`).
- Store it outside the repo. Reference it from `local.properties` or `~/.gradle/gradle.properties`.
- Add a `signingConfigs.create("release") { ... }` block and set `buildTypes.release.signingConfig = signingConfigs.getByName("release")`.
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

### 7. TMDB attribution
TMDB's API terms require visible attribution: "This product uses the TMDB API but is not endorsed or certified by TMDB" plus the TMDB logo. There is no Settings/About screen yet — add one (or surface attribution at the bottom of an existing screen) before shipping.

## Strong recommendations

### 8. TMDB token security
`buildConfigField("String", "TMDB_ACCESS_TOKEN", ...)` in `composeApp/build.gradle.kts` bakes the bearer token into the APK. R8 makes it slightly harder to extract but it's still recoverable with `apktool`. Options, in order of preference:
1. Thin proxy in front of TMDB (Cloudflare Worker / Vercel function) holding the token; the app calls the proxy.
2. Move to short-lived per-user tokens if TMDB exposes them (it doesn't currently).
3. Accept the leak risk and monitor TMDB usage for abuse.

### 9. Crash reporting
Napier (already in the project) is for logs, not prod crashes. Add Crashlytics or Sentry before staged rollout — you'll want signal on day-one launches.

### 10. Material3 is on an alpha
`material3 = "1.11.0-alpha07"` in `gradle/libs.versions.toml`. Move to the latest stable for the release build to avoid alpha-only regressions. Will require visual smoke-testing.

### 11. versionCode / versionName strategy
Currently hardcoded `versionCode = 1, versionName = "1.0"`. Before the first release, set up either:
- A `version.properties` file the gradle script reads, bumped per release, or
- CI auto-increment based on tag.

Either works — pick one before you need to ship a hotfix.

### 12. Release CI workflow
`.github/workflows/` exists. Add a workflow that runs `bundleRelease` on tag push and uploads to the internal track via `r0adkll/upload-google-play-action` or `gradle-play-publisher`.

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
