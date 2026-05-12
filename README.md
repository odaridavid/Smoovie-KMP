# Smoovie

[![codecov](https://codecov.io/github/odaridavid/Smoovie-KMP/graph/badge.svg?token=xqRlC1xr7q)](https://codecov.io/github/odaridavid/Smoovie-KMP)
![Android](https://img.shields.io/badge/Android-3DDC84?logo=android&logoColor=white)
![iOS](https://img.shields.io/badge/iOS-000000?logo=apple)

A Kotlin Multiplatform app for browsing and exploring movies, powered by
the [TMDB API](https://www.themoviedb.org/).
Targets Android and iOS with a shared Compose Multiplatform UI.

## Features

- Browse popular movies and TV shows with shimmer loading placeholders
- Featured hero pager with auto-scroll on both Movies and Shows tabs
- **Filter sheet** (genre, sort order, min rating) on Movies and Shows — filters are independent per tab, persist across app restarts, and show a badge dot on the filter icon when active
- Search movies and TV shows with debounced input
- Movie and TV show detail screens with edge-to-edge backdrop, cast, trailers, reviews, seasons,
  where to watch (streaming + rent/buy via JustWatch), FSK age rating, and a "More like this" section
- Person detail screen with biography, birthday, place of birth, and movie + TV filmography
- Watchlist — tap the bookmark on any movie or TV show detail to save it locally; browse saved
  titles from the Watchlist tab in the bottom navigation
- **Settings tab** — pick a region (defaults to your system locale) which threads into TMDB's
  popular/discover endpoints; TMDB attribution per the API ToS; app version footer
- Bottom navigation with Movies / Shows / Watchlist / Settings tabs; state preserved across tab
  switches, instant tab-to-tab transitions and sliding detail pushes
- Smooth slide transitions between destinations
- Light and dark theme support

## Project Structure

```
composeApp/src/
├── commonMain/       # Shared code (UI, ViewModels, API client, persistence)
│   └── dev/odaridavid/smoovie/
│       ├── App.kt              # NavHost + shared-transition scaffolding
│       ├── Screen.kt           # @Serializable navigation routes
│       ├── KoinInitializer.kt  # DI module
│       ├── configuration/      # TMDB image config + URL builder
│       ├── filter/             # Cross-feature filter types (MovieFilterPreferences, TvFilterPreferences, FilterPreferencesStore)
│       ├── movies/             # Movie list, detail, search, featured pager, filter, use cases
│       ├── person/             # Person detail screen + filmography
│       ├── security/           # AppCheckTokenProvider interface + registry consumed from common code
│       ├── settings/           # Settings tab: region selection, TMDB attribution, app version
│       ├── shows/              # TV shows list, detail, search, featured pager, filter, use cases
│       ├── watchlist/          # Watchlist screen, repository, Room DAO + entity
│       ├── storage/            # SmoovieDatabase (Room KMP) + platform DB builder
│       ├── utils/              # TtlCache, currentTimeMillis (expect/actual)
│       ├── ui/                 # Shared composables: FilterSheet, SearchBackHandler, SetStatusBarIcons
│       └── theme/              # SmoovieTheme (Material3, dark/light), ErrorContent, EmptyContent
├── androidMain/      # Android-specific (OkHttp engine, Room builder, Firebase init)
├── iosMain/          # iOS-specific (Darwin engine, Room builder)
└── commonTest/       # Unit tests (ViewModels, use cases, TtlCache)

composeApp/schemas/   # Exported Room schemas (tracked in git for migration review)
functions/            # Firebase Cloud Function: thin TMDB proxy that holds the API token
iosApp/               # Xcode project / Swift entry point
firebase.json         # Firebase project config (functions source dir + predeploy build)
.firebaserc           # Pins the repo to the smoovie-kmp Firebase project
```

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

### 1. Run the app locally (no setup)

The Firebase config files (`composeApp/google-services.json`, `iosApp/GoogleService-Info.plist`) and
the deployed proxy URL are committed, so cloning + opening should Just Work in debug. App Check
debug tokens are per-device and registered once (see step 3 below).

### 2. Deploy your own proxy (only if you need a separate Firebase project)

If you're forking and want your own TMDB proxy, see
[`functions/README.md`](functions/README.md). You'll create a Firebase project, set the TMDB token
as a Secret Manager secret, deploy the function, and update `TMDB_BASE_URL` in
[`AppConfig.kt`](composeApp/src/commonMain/kotlin/dev/odaridavid/smoovie/AppConfig.kt) to your
function URL.

### 3. Register your debug build for App Check

App Check rejects requests from non-authentic builds. For your local debug build to be allowed,
register its debug token once per device/simulator install:

- **Android**: run the debug build on your device, then in Logcat filter for
  `Enter this debug secret` and copy the UUID.
- **iOS**: run the debug build, then in the Xcode Debug Area look for the line printed under
  `===== Firebase App Check Debug Token =====` and copy the UUID.

Paste the UUID into **Firebase Console → App Check → Apps → ⋮ → Manage debug tokens**, give it a
name, save. The token persists in the app's local storage.

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
| [multiplatform-settings](https://github.com/russhwolf/multiplatform-settings)             | Cross-platform KV persistence (filter + region prefs) |
| [Firebase App Check](https://firebase.google.com/docs/app-check)                          | Verifies requests come from authentic builds (Play Integrity / App Attest) |
| [ktlint-gradle](https://github.com/JLLeitschuh/ktlint-gradle)                            | Code style enforcement                       |

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

## Demo

|          Android                                 |         iOS                                  |
|:-----------------------------------------:|:-----------------------------------------:|
| <video src="https://github.com/user-attachments/assets/51419a7b-d51f-4172-a259-769d14b4aa2c" width="360"/> | <video src="https://github.com/user-attachments/assets/03f1b98b-19e0-499e-bb75-d9d2956c790b" width="360"/> |
