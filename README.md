# Smoovie

[![codecov](https://codecov.io/github/odaridavid/Smoovie-KMP/graph/badge.svg?token=xqRlC1xr7q)](https://codecov.io/github/odaridavid/Smoovie-KMP)

A Kotlin Multiplatform app for browsing and exploring movies, powered by
the [TMDB API](https://www.themoviedb.org/).
Targets Android and iOS with a shared Compose Multiplatform UI.

## Features

- Browse popular movies with shimmer loading placeholders
- Featured movies hero pager with auto-scroll and shared-element transitions into the detail screen
- Genre filtering via chip row
- Search movies with debounced input
- Movie and TV show detail screens with edge-to-edge backdrop, cast, trailers, reviews, seasons,
  where to watch (streaming + rent/buy via JustWatch), FSK age rating, and a "More like this" section
- Person detail screen with biography, birthday, place of birth, and movie + TV filmography
- Watchlist — tap the bookmark on any movie or TV show detail to save it locally; browse saved
  titles from the Watchlist tab in the bottom navigation
- Bottom navigation with Movies / Shows / Watchlist tabs; state preserved across tab switches,
  instant tab-to-tab transitions and sliding detail pushes
- In-memory TTL caching for TMDB requests (1 hour) so navigating back and forth doesn't re-hit the
  network
- Typed navigation via Jetpack Compose Navigation (KMP) with serializable routes
- Smooth slide transitions between destinations, shared-element transitions on hero images
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
│       ├── movies/             # Movie list, detail, search, featured pager, use cases
│       ├── person/             # Person detail screen + filmography
│       ├── shows/              # TV shows tab (in progress — see tv-shows-plan.md)
│       ├── watchlist/          # Watchlist screen, repository, Room DAO + entity
│       ├── storage/            # SmoovieDatabase (Room KMP) + platform DB builder
│       ├── utils/              # TtlCache, currentTimeMillis (expect/actual)
│       ├── ui/                 # Shared composables (search back handler, transition locals)
│       └── theme/              # SmoovieTheme (Material3, dark/light), ErrorContent, EmptyContent
├── androidMain/      # Android-specific (BuildConfig, OkHttp engine, Room builder)
├── iosMain/          # iOS-specific (NSBundle, Darwin engine, Room builder)
└── commonTest/       # Unit tests (ViewModels, use cases, TtlCache)

composeApp/schemas/   # Exported Room schemas (tracked in git for migration review)
iosApp/               # Xcode project / Swift entry point
```

## Setup

### 1. Get a TMDB API key

Create a free account at [themoviedb.org](https://www.themoviedb.org/) and go to
**Settings → API**. You need the **API Read Access Token** (Bearer token), not the v3 API key.

### 2. Configure Android

Add your token to `local.properties` (this file is gitignored and never committed):

```properties
tmdb.access.token=YOUR_API_READ_ACCESS_TOKEN
```

Gradle reads this at build time and injects it into `BuildConfig.TMDB_ACCESS_TOKEN`.

### 3. Configure iOS

Copy the example config and fill in your token:

```shell
cp iosApp/Configuration/Config.xcconfig.example iosApp/Configuration/Config.xcconfig
```

Then edit `Config.xcconfig`:

```
TMDB_ACCESS_TOKEN=YOUR_API_READ_ACCESS_TOKEN
```

`Config.xcconfig` is gitignored — never commit it. Xcode expands the value into `Info.plist` at
build time, where Kotlin reads it via `NSBundle`.

> **Note:** Add `Config.xcconfig` to `.gitignore` to keep the token out of version control.

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

---

|          Android                                 |         iOS                                  |
|:-----------------------------------------:|:-----------------------------------------:|
| <video src="https://github.com/user-attachments/assets/9d80af08-665f-4e59-b06f-1695cf886324" width="360"/> | <video src="https://github.com/user-attachments/assets/9817f814-fb88-4b8e-8f5b-2f7d52bec108" width="360"/> |


Learn more
about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)
