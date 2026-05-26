# Smoovie

[![codecov](https://codecov.io/github/odaridavid/Smoovie-KMP/graph/badge.svg?token=xqRlC1xr7q)](https://codecov.io/github/odaridavid/Smoovie-KMP)
![Android](https://img.shields.io/badge/Android-3DDC84?logo=android&logoColor=white)
![iOS](https://img.shields.io/badge/iOS-000000?logo=apple)

A Kotlin Multiplatform app for browsing and exploring movies, powered by
the [TMDB API](https://www.themoviedb.org/).
Targets Android and iOS with a shared Compose Multiplatform UI.

<a href="https://play.google.com/store/apps/details?id=dev.odaridavid.smoovie&pli=1"><img src="https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png" alt="Get it on Google Play" height="60"/></a>

Browse popular movies and TV shows, search the TMDB catalog, drill into details (cast, trailers,
reviews, seasons, where to watch), and bookmark titles to a local watchlist. Region-aware results
are driven by a Settings picker that defaults to your system locale.

## Project Structure

The app is a single Gradle module (`:composeApp`) with one package per feature under
`composeApp/src/commonMain/kotlin/dev/odaridavid/smoovie/`. Each feature package is self-contained
(screen + ViewModel + UI models + `data/` + `domain/`); platform-native pieces live in
`androidMain/` and `iosMain/`. The Firebase Cloud Function in `functions/` proxies TMDB.

## Architecture

The TMDB API token never ships in the mobile app. Requests flow:

```
App  ──(X-Firebase-AppCheck)──▶  Cloud Function (Firebase)  ──(Bearer token)──▶  TMDB
```

The Cloud Function (`functions/src/index.ts`) verifies the Firebase App Check token (proving the
request came from an authentic build), attaches the TMDB bearer token from Google Secret Manager,
and forwards `GET /3/*` to `api.themoviedb.org`. The mobile app's `TMDB_BASE_URL` points at the
deployed function URL.

## Setup

Running the app from source requires **your own Firebase project**. The committed
`google-services.json` / `GoogleService-Info.plist` and the `TMDB_BASE_URL` in `AppConfig.kt` all
point at the production Smoovie project, and only its owner can register debug App Check tokens
against it — so cloning and running as-is will fail with "Failed to load" on every TMDB call.

If you just want to try the app, install it from
[Google Play](https://play.google.com/store/apps/details?id=dev.odaridavid.smoovie). To build from
source, work through all three steps below.

### 1. Deploy your own TMDB proxy

Create a Firebase project (Blaze plan — 2nd-gen functions require it; app-scale usage is $0),
grab a [TMDB API token](https://www.themoviedb.org/settings/api), and deploy the Cloud Function.
Full walkthrough in [`functions/README.md`](functions/README.md). You'll end up with a function
URL like `https://tmdb-proxy-<hash>-<region>.a.run.app`.

### 2. Wire your project into the app

- Add Android + iOS apps to your Firebase project, then download the generated
  `google-services.json` (Android) and `GoogleService-Info.plist` (iOS) and replace the committed
  files at `composeApp/google-services.json` and `iosApp/iosApp/GoogleService-Info.plist`.
- Update `TMDB_BASE_URL` in
  [`AppConfig.kt`](composeApp/src/commonMain/kotlin/dev/odaridavid/smoovie/AppConfig.kt) to point
  at your function URL.

### 3. Register your debug build for App Check

App Check rejects requests from non-authentic builds. For your local debug build to be allowed,
register its debug token once per device/simulator install:

- **Android**: run the debug build on your device, then in Logcat filter for
  `Enter this debug secret` and copy the UUID.
- **iOS**: run the debug build, then in the Xcode Debug Area look for the line printed under
  `===== Firebase App Check Debug Token =====` and copy the UUID.

Paste the UUID into **Firebase Console → App Check → Apps → ⋮ → Manage debug tokens** (in *your*
project from step 1), give it a name, save. The token persists in the app's local storage.

## Dependencies

| Library                                                                                   | Purpose                                      |
|-------------------------------------------------------------------------------------------|----------------------------------------------|
| [Ktor](https://ktor.io/docs/client-create-multiplatform-application.html)                 | Multiplatform HTTP client                    |
| [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization)                  | JSON parsing, typed nav route args           |
| [kotlinx.coroutines](https://github.com/Kotlin/kotlinx.coroutines)                        | Async / StateFlow                            |
| [Compose Multiplatform](https://www.jetbrains.com/compose-multiplatform/)                 | Shared UI                                    |
| [AndroidX Lifecycle](https://developer.android.com/jetpack/androidx/releases/lifecycle)   | ViewModel + viewModelScope (KMP)             |
| [AndroidX Navigation Compose](https://developer.android.com/jetpack/compose/navigation)   | Typed KMP navigation + shared transitions    |
| [Room KMP](https://developer.android.com/kotlin/multiplatform/room)                       | Local persistence for the watchlist          |
| [androidx.sqlite (bundled)](https://developer.android.com/kotlin/multiplatform/sqlite)    | Bundled SQLite driver for KMP                |
| [Koin](https://insert-koin.io)                                                            | Multiplatform dependency injection           |
| [Coil 3](https://coil-kt.github.io/coil/)                                                 | Image loading (backdrops, posters, profiles) |
| [KSP](https://github.com/google/ksp)                                                      | Room annotation processing                   |
| [multiplatform-settings](https://github.com/russhwolf/multiplatform-settings)             | Cross-platform KV persistence (filter + region + consent prefs) |
| [Firebase App Check](https://firebase.google.com/docs/app-check)                          | Verifies requests come from authentic builds (Play Integrity / App Attest) |
| [Firebase Crashlytics](https://firebase.google.com/docs/crashlytics)                      | Android crash reporting (opt-in via settings consent prompt) |
| [Napier](https://github.com/AAkira/Napier)                                                | Multiplatform logging behind the `observability.Logger` interface |
| [Play In-App Review](https://developer.android.com/guide/playcore/in-app-review)          | Android in-app review prompt (paired with `SKStoreReviewController` on iOS) |
| [ktlint-gradle](https://github.com/JLLeitschuh/ktlint-gradle)                            | Code style enforcement                       |
| [Kover](https://github.com/Kotlin/kotlinx-kover)                                          | Code coverage reports (HTML + XML, used by CI) |

## Build and Run

### Android

Run from the IDE toolbar or from the terminal:

```shell
# macOS / Linux
./gradlew :composeApp:assembleDebug

# Windows
.\gradlew.bat :composeApp:assembleDebug
```

### iOS

Open `/iosApp` in Xcode and run, or use the run configuration in Android Studio / Fleet.

### Tests

```shell
./gradlew :composeApp:allTests
```

### Lint

```shell
./gradlew :composeApp:ktlintCheck    # check (what CI runs)
./gradlew :composeApp:ktlintFormat   # auto-fix
```

### Pre-commit hook

A `hooks/pre-commit` script runs `ktlintCheck` before every commit and blocks it if there are
violations. Activate it once after cloning:

```shell
git config core.hooksPath hooks
```

If the hook blocks a commit, run `./gradlew :composeApp:ktlintFormat` to auto-fix, re-stage the
changes, then commit again.

## Privacy Policy

The privacy policy lives at [`docs/privacy-policy.html`](docs/privacy-policy.html) and is served via
Firebase Hosting:

- <https://smoovie-kmp.web.app/privacy>
- <https://smoovie-kmp.web.app/privacy-policy.html>

To redeploy after editing:

```shell
firebase deploy --only hosting
```

## Demo

|          Android                                 |         iOS                                  |
|:-----------------------------------------:|:-----------------------------------------:|
| <video src="https://github.com/user-attachments/assets/51419a7b-d51f-4172-a259-769d14b4aa2c" width="360"/> | <video src="https://github.com/user-attachments/assets/03f1b98b-19e0-499e-bb75-d9d2956c790b" width="360"/> |
