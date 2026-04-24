# Smoovie — Claude Code Context

## Project

Kotlin Multiplatform app targeting **Android and iOS**, built with Compose Multiplatform.
Single Gradle module: `:composeApp`. Powered by the TMDB API.

## Build & Test

```shell
./gradlew :composeApp:assembleDebug                          # Android debug APK
./gradlew :composeApp:compileDebugKotlinAndroid              # fast Android compile check
./gradlew :composeApp:compileKotlinIosSimulatorArm64         # fast iOS compile check
./gradlew :composeApp:compileCommonMainKotlinMetadata        # fastest common compile check
./gradlew :composeApp:testDebugUnitTest                      # common + Android tests
./gradlew :composeApp:allTests                               # all targets (iOS link may fail locally with xcrun 72)
./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64    # iOS simulator framework
./gradlew :composeApp:linkDebugFrameworkIosArm64             # iOS device framework
```

`:composeApp:allTests` sometimes fails at `linkDebugTestIosSimulatorArm64` with a local `xcrun` error 72 — treat that as an environment issue, not a code failure. Fall back to `testDebugUnitTest` + `compileKotlinIosSimulatorArm64` to confirm KMP correctness.

## File Structure

```
composeApp/
├── schemas/                              # Exported Room schemas — check in migrations
└── src/
    ├── commonMain/
    │   ├── composeResources/values/strings.xml
    │   └── kotlin/dev/odaridavid/smoovie/
    │       ├── App.kt                    # Scaffold + Material3 NavigationBar (Movies / Shows / Watchlist) wrapping NavHost; slide transitions for detail pushes, instant for tab-to-tab
    │       ├── Screen.kt                 # @Serializable nav routes + UI↔route converters
    │       ├── AppConfig.kt              # expect val tmdbApiKey + TMDB_BASE_URL const
    │       ├── KoinInitializer.kt        # appModule + initKoin { setup }
    │       ├── PlatformModule.kt         # expect val platformModule: Module
    │       ├── configuration/            # ConfigurationStore, ConfigurationRepository(Impl), LoadConfigurationUseCase, ImagesConfiguration (image base URL + size enums drive all poster/backdrop/profile URLs)
    │       ├── movies/
    │       │   ├── MoviesScreen.kt / MoviesViewModel.kt / MoviesUiState.kt (wraps MoviesUiState in a MoviesScreenState holding searchQuery, genres, featuredMovies)
    │       │   ├── MovieDetailScreen.kt / MovieDetailViewModel.kt / MovieDetailUiState.kt / MovieDetailUiModel.kt
    │       │   ├── MovieUiModel.kt / MovieUiMapper.kt
    │       │   ├── components/           # HeroSection, FeaturedMoviesPager, CastSection, etc.
    │       │   ├── data/                 # MoviesRepositoryImpl (Ktor + TtlCache), DTOs
    │       │   └── domain/               # MoviesRepository interface + use cases
    │       ├── person/
    │       │   ├── PersonDetailScreen.kt / PersonDetailViewModel.kt / PersonDetailUiModel.kt
    │       │   ├── components/           # HeaderFrame, PersonPhoto, ShimmerPersonDetail
    │       │   ├── data/                 # PersonRepositoryImpl (Ktor + TtlCache), DTOs
    │       │   └── domain/               # PersonRepository interface + GetPersonDetailUseCase
    │       ├── shows/                    # Placeholder TV-shows surface (tab destination); data/domain/components land here as Phases 2-3 of tv-shows-plan.md roll in
    │       ├── watchlist/
    │       │   ├── WatchlistScreen.kt / WatchlistViewModel.kt / WatchlistUiState.kt
    │       │   ├── data/                 # WatchlistDao, WatchlistMovieEntity, WatchlistRepositoryImpl
    │       │   └── domain/               # WatchlistRepository, WatchlistEntry, ObserveWatchlist/ToggleWatchlist/ObserveIsInWatchlist use cases
    │       ├── storage/                  # SmoovieDatabase (Room KMP), DatabaseBuilderFactory (expect)
    │       ├── theme/                    # SmoovieTheme, ErrorContent, EmptyContent, Type.kt
    │       ├── ui/                       # SearchBackHandler (expect)
    │       └── utils/                    # TtlCache, currentTimeMillis (expect), PreviewData
    ├── androidMain/kotlin/dev/odaridavid/smoovie/
    │   ├── SmoovieApplication.kt         # initKoin { androidContext(this) }
    │   ├── MainActivity.kt
    │   ├── AppConfig.android.kt          # BuildConfig.TMDB_ACCESS_TOKEN
    │   ├── PlatformModule.android.kt     # binds DatabaseBuilderFactory(context)
    │   ├── ui/BackHandler.android.kt
    │   ├── storage/DatabaseBuilder.android.kt
    │   └── utils/CurrentTime.android.kt
    ├── iosMain/kotlin/dev/odaridavid/smoovie/
    │   ├── MainViewController.kt
    │   ├── AppConfig.ios.kt              # reads from NSBundle (Info.plist)
    │   ├── PlatformModule.ios.kt         # binds DatabaseBuilderFactory()
    │   ├── ui/BackHandler.ios.kt
    │   ├── storage/DatabaseBuilder.ios.kt
    │   └── utils/CurrentTime.ios.kt
    └── commonTest/kotlin/dev/odaridavid/smoovie/
        ├── Fake*Repository.kt            # shared fakes (Movies, Person, Watchlist, Configuration)
        ├── configuration/ConfigurationStoreTest.kt
        ├── movies/Movie*Test.kt / Movie*ViewModelTest.kt
        ├── person/Person*Test.kt / Person*ViewModelTest.kt
        └── utils/TtlCacheTest.kt

iosApp/                                   # Xcode project / Swift entry point
```

## Architecture

### Layers

- **UI**: Composables in each feature package (`movies/`, `person/`, `watchlist/`). Each screen has a stateful wrapper `fun FooScreen(viewModel, ...)` plus a stateless `internal fun FooContent(state, ...)` targeted by previews.
- **ViewModel**: `androidx.lifecycle.ViewModel` from the multiplatform lifecycle artifact. State via `MutableStateFlow<*UiState>` exposed as `StateFlow`. VMs depend on **use cases**, not repositories.
- **Domain**: Interface-only repositories plus `XxxUseCase` classes (`suspend operator fun invoke(...)`) in each feature's `domain/` package. Use cases may return UI models — `GetMovieDetailUseCase` returns `MovieDetailUiModel`, mapping inside via `ConfigurationStore` URL builders.
- **Data**: `*RepositoryImpl` classes (Ktor-backed) in each feature's `data/` package. Network repos wrap their fetchers in `TtlCache` (1-hour TTL) so navigating back and forth doesn't re-hit TMDB.
- **Storage**: `SmoovieDatabase` (Room KMP) lives in `storage/`. Feature-specific DAOs/entities live with their feature (e.g. `watchlist/data/WatchlistDao.kt`); the DB just references them. Add a new DAO by adding a new entity class to the `@Database(entities = [...])` list and an abstract accessor on `SmoovieDatabase`.

### DI

- Koin. All common bindings in `KoinInitializer.kt` → `private val appModule`.
- `expect val platformModule: Module` is implemented per-platform (`PlatformModule.android.kt` / `PlatformModule.ios.kt`); this is where `DatabaseBuilderFactory` is bound (Android needs a `Context`, iOS doesn't).
- `initKoin { ... }` takes a `KoinApplication.() -> Unit` block; Android passes `androidContext(this@SmoovieApplication)`.
- **Adding a new use case or VM**: declare it in `appModule` (`single { ... }` for use cases, `viewModel { ... }` for VMs). Parameterised VMs use `viewModel { (id: Int) -> ... }` and resolve at the call site via `koinViewModel(key = ..., parameters = { parametersOf(id) })`.

### Navigation

- Jetpack Compose Navigation (KMP variant, `org.jetbrains.androidx.navigation:navigation-compose`).
- Routes live in `Screen.kt` as top-level `@Serializable` types (`data object MoviesRoute`, `data class MovieDetailRoute(...)`, etc.) plus `toRoute()` / `toUiModel()` converters.
- `App.kt` wraps the `NavHost` in a `Scaffold` with a Material3 `NavigationBar`. Three top-level tabs: **Movies**, **Shows**, **Watchlist**. The bar is shown only when the current destination satisfies `NavDestination.isTopLevelTab()` — hidden on detail pushes (`MovieDetailRoute`, `PersonDetailRoute`).
- Tab clicks go through `NavHostController.navigateToTab(route)`, which uses the standard Material pattern: `popUpTo(startDestination) { saveState = true } + launchSingleTop + restoreState` so each tab preserves its scroll / filter / loaded-page state across switches.
- **Transitions**: the `NavHost` defines slide-horizontal enter/exit/pop defaults for detail pushes. Each tab destination (`composable<MoviesRoute>`, `composable<ShowsRoute>`, `composable<WatchlistRoute>`) overrides all four transitions with `if (initialState.destination.isTopLevelTab()) EnterTransition.None else null` (and mirror for exit/pop), returning `null` to fall through to the NavHost default. Net effect: tab ↔ tab is instant, tab → detail slides, detail pop → tab slides back.
- **Adding a new detail destination**: add a `@Serializable` route in `Screen.kt`, add a `composable<Route> { ... }` block in `App.kt` (no transition overrides — inherits the default slide), wire up back/forward with `navController.navigate(...)` / `navController.navigateUp()`.
- **Adding a new top-level tab**: (1) add the `@Serializable data object` route, (2) add it to `isTopLevelTab()`, (3) add a `NavigationBarItem` in `AppBottomBar`, (4) add a `composable<Route>` block with the four tab-transition overrides matching the existing tabs. Tab destinations don't take an `onBack` — back-gesture pops the tab stack, not the tab itself.

### In-memory request cache

- `utils/TtlCache.kt` — generic `TtlCache<K, V>(ttlMillis, now)`. `Mutex`-protected map; `getOrFetch(key, fetch)` runs `fetch` outside the lock so different keys don't block each other.
- Each network repo holds one `TtlCache` per endpoint, keyed by the query params (`page`, `SearchKey(query, page)`, `GenreKey(genreId, page)`, `Unit` for the static genres list, `Int` for detail). TTL constant lives on the repo, currently 1 hour.

### Expect/actual

- Use for platform-native integration (native SDKs, no cross-platform wrapper libs).
- Existing: `tmdbApiKey` (AppConfig), `platformModule` (DI), `DatabaseBuilderFactory` (Room), `currentTimeMillis` (utils), `SearchBackHandler` (UI back-handler), Room's own `SmoovieDatabaseConstructor` (generated by KSP).
- `-Xexpect-actual-classes` is enabled in `composeApp/build.gradle.kts` to silence the Beta warning.

## API Key Configuration

### Android
Add to `local.properties` (gitignored):
```
tmdb.access.token=YOUR_TOKEN
```
Gradle injects it into `BuildConfig.TMDB_ACCESS_TOKEN` via `buildConfigField`. `SmoovieApplication` bootstraps Koin with `androidContext(this)` and is registered in `AndroidManifest.xml` via `android:name=".SmoovieApplication"` — don't replace it without updating the manifest.

### iOS
Copy and fill in `iosApp/Configuration/Config.xcconfig` (gitignored):
```
TMDB_ACCESS_TOKEN=YOUR_TOKEN
```
Xcode expands this into `Info.plist`, read at runtime via `NSBundle`.

Use the **API Read Access Token** (Bearer token) from TMDB settings, not the v3 API key.

## Conventions

### No wildcard imports
Ktlint enforces specific imports. Always import individual symbols.

### String resources
UI strings live in `composeApp/src/commonMain/composeResources/values/strings.xml`. Use `stringResource(Res.string.key_name)` at call sites. Mark brand names / non-translatable strings with `translatable="false"`.

### Comments
Default to none. Let well-named identifiers do the talking. Add a single-line comment only when the *why* is non-obvious (a workaround, a subtle invariant, a hidden constraint). No multi-paragraph docstrings.

### Previews
- `@PreviewLightDark` (preferred) on stateless `*Content` composables, which are `internal` so previews in the same module can reach them. A handful of older files (`theme/EmptyContent.kt`, `theme/ErrorContent.kt`) still use `@Preview` + `@Preview(uiMode = UI_MODE_NIGHT_YES)` — prefer `@PreviewLightDark` for new code.
- Preview functions are `private` in PascalCase — ignore ktlint naming warnings on these.
- Shared preview data fixtures live in `utils/PreviewData.kt`.

### Test naming
Kotlin backtick syntax with Given/When/Then:
```kotlin
fun `given <context> - when <action> - then <result>`()
```

### ViewModel tests
- Set `Dispatchers.setMain(UnconfinedTestDispatcher())` in `@BeforeTest` so `viewModelScope` runs eagerly.
- Use the shared fakes in `commonTest/` root (`FakeMoviesRepository`, `FakeWatchlistRepository`, `FakePersonRepository`, `FakeConfigurationRepository`) — they live at test-root level and are reused across test files.
- `@AfterTest` always calls `Dispatchers.resetMain()`.
- When a VM depends on use cases, wire the fakes into fresh use-case instances inside each test's `buildViewModel(...)` helper rather than mocking the use case itself.

### UI-model mapping
Map domain → UI model in use cases (see `GetMovieDetailUseCase`) or ViewModels — not in composables. Composables receive ready-to-display data. It's fine for use cases to accept image-URL resolver lambdas (as `GetMovieDetailUseCase` does) because those lambdas resolve against `ConfigurationStore` at mapping time; just don't pass resolver lambdas *down the composable tree*.

### Error retry
The movies screen's retry button calls `MoviesViewModel.retry()`, which runs the full `loadData()` (configuration + genres + first page + featuredMovies population). `loadMovies()` is the lighter path used by genre switches where config is already loaded. If you add a new initial-load side effect, put it in `loadData()` and retry will pick it up automatically.

## Key Dependencies

| Library                            | Purpose                                                           |
|------------------------------------|-------------------------------------------------------------------|
| Ktor                               | Multiplatform HTTP client (OkHttp engine on Android, Darwin iOS)  |
| kotlinx.serialization              | JSON parsing + typed nav-route args                               |
| Compose Multiplatform              | Shared UI                                                         |
| AndroidX Lifecycle (KMP)           | ViewModel + viewModelScope                                        |
| AndroidX Navigation Compose (KMP)  | Typed navigation + animated content scope                         |
| Room KMP + sqlite-bundled          | Local persistence (watchlist)                                     |
| Koin                               | Multiplatform DI                                                  |
| Coil 3                             | Image loading                                                     |
| KSP                                | Room annotation processing                                        |
