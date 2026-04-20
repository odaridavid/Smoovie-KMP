# Smoovie

A Kotlin Multiplatform app for browsing and exploring movies, powered by the [TMDB API](https://www.themoviedb.org/).
Targets Android and iOS with a shared Compose Multiplatform UI.

## Features

- Browse popular movies with shimmer loading placeholders
- Search movies with debounced input
- Movie detail screen with edge-to-edge backdrop image, gradient scrims, and enriched data (genres, runtime, tagline)
- Smooth slide transitions between list and detail screens
- Light and dark theme support

## Project Structure

```
composeApp/src/
├── commonMain/       # Shared code (UI, ViewModels, API client)
│   └── dev/odaridavid/smoovie/
│       ├── configuration/   # TMDB image config + URL builder
│       ├── movies/          # Movie list, detail, search, models, repositories
│       └── theme/           # SmoovieTheme (Material3, dark/light)
├── androidMain/      # Android-specific (BuildConfig, OkHttp engine)
├── iosMain/          # iOS-specific (NSBundle, Darwin engine)
└── commonTest/       # Unit tests

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

`Config.xcconfig` is gitignored — never commit it. Xcode expands the value into `Info.plist` at build time, where Kotlin reads it via `NSBundle`.

> **Note:** Add `Config.xcconfig` to `.gitignore` to keep the token out of version control.

## Dependencies

| Library | Purpose |
|---|---|
| [Ktor](https://ktor.io/docs/client-create-multiplatform-application.html) | Multiplatform HTTP client |
| [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization) | JSON parsing |
| [kotlinx.coroutines](https://github.com/Kotlin/kotlinx.coroutines) | Async / StateFlow |
| [Compose Multiplatform](https://www.jetbrains.com/compose-multiplatform/) | Shared UI |
| [AndroidX Lifecycle](https://developer.android.com/jetpack/androidx/releases/lifecycle) | ViewModel + viewModelScope (KMP) |
| [Koin](https://insert-koin.io) | Multiplatform dependency injection |

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

---

|                                           |
|:-----------------------------------------:|
| <img src="/docs/demo_1.gif" width="360"/> | 

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)