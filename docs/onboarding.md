# Onboarding

How to set up locally and get a debug build running. For shipping a release, see `releasing.md`.

## Prerequisites

- JDK 17 (Temurin). The CI uses 17; local builds fail with 11 or 21.
- Android Studio (any recent stable) or just the Android SDK + Gradle.
- Xcode 15+ if you'll touch the iOS side.
- macOS for iOS work; Linux/Windows is fine for Android-only.

## Clone and run

```shell
git clone git@github.com:odaridavid/smoovie.git
cd smoovie
./gradlew :composeApp:assembleDebug
./gradlew :composeApp:installDebug   # if a device/emulator is attached
```

You can build and run debug without any secrets. Movies will not load yet — the app needs an App Check debug token registered with Firebase.

## Registering a debug App Check token

Without this, the Cloud Function rejects every request from your debug build with 401 and the Movies/Shows lists show "Failed to load."

1. Launch the debug build on your device once.
2. Filter logcat for `Enter this debug secret` — copy the UUID it prints.
3. Firebase Console → **App Check** → **Apps** → the Smoovie Android app → ⋮ menu → **Manage debug tokens** → **Add debug token** → paste, give it a name (e.g. "alice-pixel-7").
4. Force-stop and reopen the app. Tokens are cached for 1 hour; restarting bypasses the cache.

iOS debug builds print an equivalent token between `===== Firebase App Check Debug Token =====` lines in the Xcode console.

## Daily commands

```shell
./gradlew :composeApp:compileDebugKotlinAndroid          # fast Android compile check
./gradlew :composeApp:compileKotlinIosSimulatorArm64     # fast iOS compile check
./gradlew :composeApp:compileCommonMainKotlinMetadata    # fastest common compile check
./gradlew :composeApp:testDebugUnitTest                  # common + Android tests
./gradlew :composeApp:ktlintCheck                        # lint
./gradlew :composeApp:ktlintFormat                       # autofix
./gradlew :composeApp:koverHtmlReportDebug               # coverage report
```

## Working on release-only features locally

For things that only manifest in release (R8 stripping, ProGuard rules, App Check Play Integrity, signed APK paths), you'll need the upload keystore. It's not in the repo — ask the maintainer.

1. Drop the keystore somewhere outside the repo, e.g. `~/.android/smoovie-upload.keystore`.
2. Add to `local.properties` (gitignored):
   ```
   release.store.file=/Users/you/.android/smoovie-upload.keystore
   release.store.password=<password>
   release.key.alias=smoovie
   release.key.password=<password>
   ```
3. Build: `./gradlew :composeApp:assembleRelease` for an installable APK, or `:bundleRelease` for the Play-uploadable AAB.

A release-built APK installed via `adb install` won't pass App Check — Play Integrity returns `UNRECOGNIZED_VERSION` for sideloaded installs and Firebase refuses to mint a token. To fully exercise the release path you have to install via Play Store internal testing.

## Known quirks

- **Material3 is on an alpha.** `org.jetbrains.compose.material3` has no stable release; the JetBrains KMP wrapper publishes alphas only. Monitor [Compose Multiplatform releases](https://github.com/JetBrains/compose-multiplatform/releases) and bump when stable lands.
- **`:composeApp:allTests` is locally flaky.** `linkDebugTestIosSimulatorArm64` sometimes fails with `xcrun` error 72 — environment issue, not a code failure. Fall back to `testDebugUnitTest` + `compileKotlinIosSimulatorArm64` to confirm KMP correctness.
