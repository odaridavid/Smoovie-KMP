# Smoovie — Claude Code Context

## Project

Kotlin Multiplatform app targeting **Android and iOS**, built with Compose Multiplatform.
Single Gradle module: `:composeApp`.

## File Structure

```
composeApp/src/
├── commonMain/kotlin/dev/odaridavid/smoovie/
│   ├── App.kt                          # Root composable — wires theme + ViewModel
│   ├── AppConfig.kt                    # expect val tmdbApiKey: String
│   ├── data/
│   │   ├── MoviesRepository.kt         # Interface — ViewModel depends on this, not TmdbApi
│   │   ├── TmdbApi.kt                  # Ktor HTTP client, implements MoviesRepository
│   │   └── model/Movie.kt              # @Serializable data classes (Movie, MoviesResponse)
│   └── ui/
│       ├── theme/
│       │   ├── Theme.kt                # SmoovieTheme wrapping MaterialTheme
│       │   ├── ErrorContent.kt         # Reusable error + retry composable
│       │   └── PreviewThemes.kt        # Preview util — renders light + dark in one preview
│       └── movies/
│           ├── MoviesScreen.kt         # Stateful wrapper — passes ViewModel state to MoviesContent
│           ├── MoviesUiState.kt        # sealed interface: Loading / Success / Error
│           ├── MoviesViewModel.kt      # Holds StateFlow<MoviesUiState>, calls MoviesRepository
│           ├── MovieCard.kt            # Single movie card composable
│           └── PreviewData.kt          # Shared preview movie fixtures
├── androidMain/
│   ├── AppConfig.android.kt            # actual tmdbApiKey = BuildConfig.TMDB_ACCESS_TOKEN
│   └── MainActivity.kt
├── iosMain/
│   ├── AppConfig.ios.kt                # actual tmdbApiKey from NSBundle (Info.plist)
│   └── MainViewController.kt
└── commonTest/
    └── MoviesViewModelTest.kt          # ViewModel unit tests
```

## Architecture

- **ViewModel → interface**: `MoviesViewModel` depends on `MoviesRepository`, not `TmdbApi` directly. This keeps the ViewModel testable without a real network.
- **Stateless composables**: each screen has a stateful wrapper (takes `ViewModel`) and a stateless `*Content` composable (takes `UiState` + callbacks). Previews target the stateless composable.
- **`expect`/`actual` for config**: `tmdbApiKey` is declared in `commonMain` and implemented per platform — Android reads `BuildConfig`, iOS reads `NSBundle`.

## API Key Configuration

### Android
Add to `local.properties` (gitignored):
```
tmdb.access.token=YOUR_TOKEN
```
Gradle injects it into `BuildConfig.TMDB_ACCESS_TOKEN` via `buildConfigField`.

### iOS
Copy and fill in `iosApp/Configuration/Config.xcconfig` (gitignored):
```
TMDB_ACCESS_TOKEN=YOUR_TOKEN
```
Xcode expands this into `Info.plist`, read at runtime via `NSBundle`.

Use the **API Read Access Token** (Bearer token) from TMDB settings, not the v3 API key.

## Key Conventions

### No wildcard imports
Ktlint enforces specific imports. Always import individual symbols, not `.*`.

### String resources
UI strings live in `composeApp/src/commonMain/composeResources/values/strings.xml`.
Use `stringResource(Res.string.key_name)` at call sites.
Mark brand names / non-translatable strings with `translatable="false"`.

### Previews
- Use `@Preview` + `@Preview(uiMode = UI_MODE_NIGHT_YES)` on each preview function for light/dark variants.
- Wrap content in `PreviewThemes { }` when a single function should show both themes stacked.
- Preview functions are `private` and named in PascalCase (Compose convention — ignore ktlint naming warnings on these).
- Stateless `*Content` composables are `internal` so preview functions in the same module can reach them.

### Test naming
Use Kotlin backtick syntax with Given/When/Then:
```kotlin
fun `given <context>, when <action>, then <result>`()
```

### ViewModel tests
- Set `Dispatchers.setMain(UnconfinedTestDispatcher())` in `@BeforeTest` so `viewModelScope` runs eagerly.
- Use `FakeMoviesRepository` (implements `MoviesRepository`) — defined inside the test file, not shared.
- `@AfterTest` always calls `Dispatchers.resetMain()`.

## Running Tests

```shell
./gradlew :composeApp:allTests
```

## Build

```shell
# Android debug APK
./gradlew :composeApp:assembleDebug

# iOS framework (physical device)
./gradlew :composeApp:linkDebugFrameworkIosArm64

# iOS framework (simulator)
./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64
```