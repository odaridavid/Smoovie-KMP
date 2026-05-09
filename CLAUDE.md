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
./gradlew :composeApp:ktlintCheck                            # lint check (runs in CI)
./gradlew :composeApp:ktlintFormat                           # auto-fix style violations
./gradlew :composeApp:koverHtmlReportDebug                   # HTML coverage report → build/reports/kover/htmlDebug/
./gradlew :composeApp:koverXmlReportDebug                    # XML coverage report → build/reports/kover/reportDebug.xml (runs in CI)
```

`:composeApp:allTests` sometimes fails at `linkDebugTestIosSimulatorArm64` with a local `xcrun` error 72 — treat that as an environment issue, not a code failure. Fall back to `testDebugUnitTest` + `compileKotlinIosSimulatorArm64` to confirm KMP correctness.

### Cloud Function

```shell
cd functions && npm install                        # one-time
firebase functions:secrets:set TMDB_TOKEN          # set/rotate the upstream token
firebase deploy --only functions                   # deploy
firebase functions:log                             # tail logs
npm run build                                      # type-check locally without deploying
```

Firebase project requires the **Blaze** plan (2nd-gen functions need it); free-tier usage is $0 for app-scale traffic. A budget alert at $5/month is recommended.

## File Structure

```
composeApp/
├── schemas/                              # Exported Room schemas — check in migrations
├── google-services.json                  # Firebase project config (committed; not a secret)
└── src/
    ├── commonMain/kotlin/dev/odaridavid/smoovie/
    │   ├── App.kt, Screen.kt             # NavHost + Scaffold/NavigationBar; @Serializable routes
    │   ├── AppConfig.kt                  # `internal const val TMDB_BASE_URL` (points at the Cloud Function)
    │   ├── KoinInitializer.kt            # appModule wiring; initKoin { setup }
    │   ├── PlatformModule.kt             # expect val platformModule: Module
    │   ├── filter/                       # cross-feature: MovieFilterPreferences, TvFilterPreferences, FilterPreferencesStore
    │   ├── security/                     # AppCheckTokenProvider interface + registry + Ktor plugin (X-Firebase-AppCheck header)
    │   ├── settings/                     # Settings tab: Region picker, TMDB attribution, app version footer; SettingsPreferencesStore
    │   ├── <feature>/                    # one package per feature (movies, shows, person, watchlist, configuration)
    │   │   ├── <Feature>Screen.kt + <Feature>ViewModel.kt + <Feature>UiState.kt + UI models
    │   │   ├── components/               # feature-local composables
    │   │   ├── data/                     # <Feature>RepositoryImpl, DTOs (and DAO/Entity for watchlist)
    │   │   └── domain/                   # repository interface + use cases
    │   ├── storage/                      # Room database + migrations + expect DatabaseBuilderFactory
    │   ├── theme/, ui/, utils/           # shared composables (FilterSheet in ui/), expect helpers, TtlCache etc.
    │   └── composeResources/             # strings.xml + drawables (incl. tmdb_logo.xml AVD)
    ├── androidMain/                      # actuals + SmoovieApplication (Firebase + AppCheck init), MainActivity, AndroidAppCheckTokenProvider
    ├── debug/, release/                  # Android variant-only sources for App Check factory split (Debug vs PlayIntegrity providers)
    ├── iosMain/                          # actuals (mirrors androidMain) + MainViewController
    └── commonTest/kotlin/dev/odaridavid/smoovie/
        ├── Fake*Repository.kt            # shared test doubles at test-root
        └── <feature>/                    # tests mirror commonMain feature layout

functions/                                # Firebase Cloud Function (TypeScript): TMDB proxy with App Check enforcement
firebase.json, .firebaserc                # Firebase project pin + functions source declaration
iosApp/                                   # Xcode project / Swift entry point (iOSApp.swift owns Firebase init + IosAppCheckTokenProvider)
iosApp/iosApp/GoogleService-Info.plist    # Firebase iOS config (committed; not a secret)
```

Each feature package is self-contained (UI + ViewModel + UiState + UI models + components + data + domain). Look inside the package you're touching to see the exact files; the structure is consistent across features, so once you've seen one (`movies/` is a good reference), the rest follow the same shape.

## Architecture

### Layers

- **UI**: Composables in each feature package (`movies/`, `person/`, `watchlist/`). Each screen has a stateful wrapper `fun FooScreen(viewModel, ...)` plus a stateless `internal fun FooContent(state, ...)` targeted by previews.
- **ViewModel**: `androidx.lifecycle.ViewModel` from the multiplatform lifecycle artifact. State via `MutableStateFlow<*UiState>` exposed as `StateFlow`. VMs depend on **use cases**, not repositories.
- **Domain**: Interface-only repositories plus `XxxUseCase` classes (`suspend operator fun invoke(...)`) in each feature's `domain/` package. Use cases may return UI models — `GetMovieDetailUseCase` returns `MovieDetailUiModel`, mapping inside via `ConfigurationStore` URL builders.
- **Data**: `*RepositoryImpl` classes (Ktor-backed) in each feature's `data/` package. Network repos wrap their fetchers in `TtlCache` (1-hour TTL) so navigating back and forth doesn't re-hit TMDB.
- **Storage**: `SmoovieDatabase` (Room KMP) lives in `storage/`. Feature-specific DAOs/entities live with their feature (e.g. `watchlist/data/WatchlistDao.kt`); the DB just references them. Add a new DAO by adding a new entity class to the `@Database(entities = [...])` list and an abstract accessor on `SmoovieDatabase`. Migrations live in `storage/Migrations.kt` and are passed to the builder via `.addMigrations(...)` in `KoinInitializer`. When you bump `version`, write a migration (no `fallbackToDestructiveMigration` is configured) and Room KSP will export the new schema under `composeApp/schemas/.../<version>.json` — commit that file.

### DI

- Koin. All common bindings in `KoinInitializer.kt` → `private val appModule`.
- `expect val platformModule: Module` is implemented per-platform (`PlatformModule.android.kt` / `PlatformModule.ios.kt`); this is where `DatabaseBuilderFactory` is bound (Android needs a `Context`, iOS doesn't).
- `initKoin { ... }` takes a `KoinApplication.() -> Unit` block; Android passes `androidContext(this@SmoovieApplication)`.
- **Adding a new use case or VM**: declare it in `appModule` (`single { ... }` for use cases, `viewModel { ... }` for VMs). Parameterised VMs use `viewModel { (id: Int) -> ... }` and resolve at the call site via `koinViewModel(key = ..., parameters = { parametersOf(id) })`.

### Navigation

- Jetpack Compose Navigation (KMP variant, `org.jetbrains.androidx.navigation:navigation-compose`).
- Routes live in `Screen.kt` as top-level `@Serializable` types (`data object MoviesRoute`, `data class MovieDetailRoute(...)`, etc.) plus `toRoute()` / `toUiModel()` converters.
- `App.kt` wraps the `NavHost` in a `Scaffold` with a Material3 `NavigationBar`. Four top-level tabs: **Movies**, **Shows**, **Watchlist**, **Settings**. The bar is shown only when the current destination satisfies `NavDestination.isTopLevelTab()` — hidden on detail pushes (`MovieDetailRoute`, `PersonDetailRoute`).
- Tab clicks go through `NavHostController.navigateToTab(route)`, which uses the standard Material pattern: `popUpTo(startDestination) { saveState = true } + launchSingleTop + restoreState` so each tab preserves its scroll / filter / loaded-page state across switches.
- **Transitions**: the `NavHost` defines slide-horizontal enter/exit/pop defaults for detail pushes. Each tab destination (`composable<MoviesRoute>`, `composable<ShowsRoute>`, `composable<WatchlistRoute>`) overrides all four transitions with `if (initialState.destination.isTopLevelTab()) EnterTransition.None else null` (and mirror for exit/pop), returning `null` to fall through to the NavHost default. Net effect: tab ↔ tab is instant, tab → detail slides, detail pop → tab slides back.
- **Adding a new detail destination**: add a `@Serializable` route in `Screen.kt`, add a `composable<Route> { ... }` block in `App.kt` (no transition overrides — inherits the default slide), wire up back/forward with `navController.navigate(...)` / `navController.navigateUp()`.
- **Adding a new top-level tab**: (1) add the `@Serializable data object` route, (2) add it to `isTopLevelTab()`, (3) add a `NavigationBarItem` in `AppBottomBar`, (4) add a `composable<Route>` block with the four tab-transition overrides matching the existing tabs. Tab destinations don't take an `onBack` — back-gesture pops the tab stack, not the tab itself.

### In-memory request cache

- `utils/TtlCache.kt` — generic `TtlCache<K, V>(ttlMillis, now)`. `Mutex`-protected map; `getOrFetch(key, fetch)` runs `fetch` outside the lock so different keys don't block each other.
- Each network repo holds one `TtlCache` per endpoint, keyed by the query params (`page`, `SearchKey(query, page)`, `DiscoverKey(genreId?, sortBy, minRating, page)`, `Unit` for the static genres list, `Int` for detail). TTL constant lives on the repo, currently 1 hour.

### Expect/actual

- Use for platform-native integration (native SDKs, no cross-platform wrapper libs).
- Existing: `platformModule` (DI), `DatabaseBuilderFactory` (Room), `currentTimeMillis` (utils), `SearchBackHandler` (UI back-handler), `systemRegionCode` (settings/), `appVersionInfo` (settings/), Room's own `SmoovieDatabaseConstructor` (generated by KSP).
- `-Xexpect-actual-classes` is enabled in `composeApp/build.gradle.kts` to silence the Beta warning.
- The Android variant-only source dirs (`src/debug/kotlin/`, `src/release/kotlin/`) emit a deprecation warning suggesting `androidDebug`/`androidRelease`. Migration is pending.

## TMDB Proxy + App Check

The TMDB API token never ships in the mobile app. Network flow:

```
App  ──(X-Firebase-AppCheck header)──▶  Cloud Function (Firebase)  ──(Bearer token)──▶  TMDB
```

### Cloud Function (`functions/src/index.ts`)

2nd-gen `onRequest` deployed to `europe-west1`. Verifies the App Check token via `firebase-admin`, then forwards `GET /3/*` to `api.themoviedb.org` with the bearer header from a Secret Manager secret (`TMDB_TOKEN`). Returns 401 on missing/invalid App Check, 405 on non-GET, 404 on paths not under `/3/`. Deploy: `firebase deploy --only functions`. Set the secret: `firebase functions:secrets:set TMDB_TOKEN`. Detailed steps in `functions/README.md`.

`TMDB_BASE_URL` in `commonMain/AppConfig.kt` points at the deployed function URL — change it there if you re-deploy under a different name/region/project.

### App Check on the client

- **Token API**: `security/AppCheck.kt` declares `interface AppCheckTokenProvider { fun fetchToken(callback) }` plus `object AppCheckTokenProviderRegistry { var instance: AppCheckTokenProvider? }` and a `suspend fun fetchAppCheckToken()` that converts the callback to suspend.
- **Android**: `SmoovieApplication.onCreate` runs `FirebaseApp.initializeApp` + `FirebaseAppCheck.installAppCheckProviderFactory(appCheckProviderFactory())`. The factory is variant-split: `src/debug/.../AppCheckProviderFactory.debug.kt` returns `DebugAppCheckProviderFactory`, `src/release/.../AppCheckProviderFactory.release.kt` returns `PlayIntegrityAppCheckProviderFactory`. Then registers `AndroidAppCheckTokenProvider` into the registry.
- **iOS**: `iOSApp.swift` runs `FirebaseApp.configure()` + `AppCheck.setAppCheckProviderFactory(...)` (App Attest in release, Debug factory in DEBUG). A Swift class `IosAppCheckTokenProvider: NSObject, AppCheckTokenProvider` calls `AppCheck.appCheck().token(forcingRefresh:)` and the iOS app injects it into `AppCheckTokenProviderRegistry.shared`.
- **Ktor wiring**: `KoinInitializer` installs the `AppCheckHeader` plugin (defined in `security/AppCheck.kt` via `createClientPlugin`). The plugin's `onRequest` calls `fetchAppCheckToken()` and attaches `X-Firebase-AppCheck` per request. No bearer header is added on the client — the proxy attaches it server-side.

### Debug-token registration (per device)

App Check rejects unauthorized debug builds. To allow yours: run the debug build, copy the printed UUID, paste into Firebase Console → App Check → Apps → ⋮ → Manage debug tokens.

- **Android logcat filter**: `Enter this debug secret`
- **iOS Xcode console**: explicit `print` block in `iOSApp.swift` between `===== Firebase App Check Debug Token =====` lines

`SmoovieApplication` is registered in `AndroidManifest.xml` via `android:name=".SmoovieApplication"` — don't replace it without updating the manifest.

## Conventions

### Code style (ktlint)
`jlleitschuh/ktlint-gradle 12.1.2` is applied to `:composeApp`. Configuration lives in `.editorconfig` at the repo root. Key rules:

- No wildcard imports — always import individual symbols
- Max line length: 140
- `@Composable` functions are exempt from the lowercase function-naming rule
- The `filename` rule is disabled — KMP expect/actual files use platform suffixes (e.g. `DatabaseBuilder.android.kt`) that would otherwise conflict
- `MainViewController.kt` carries `@Suppress("ktlint:standard:function-naming")` because Swift must call it by its PascalCase name
- Generated sources under `build/` are excluded from linting

Run `ktlintFormat` before committing to auto-fix formatting; `ktlintCheck` is what CI enforces.

### String resources
UI strings live in `composeApp/src/commonMain/composeResources/values/strings.xml`. Use `stringResource(Res.string.key_name)` at call sites. Mark brand names / non-translatable strings with `translatable="false"`.

### Comments
Default to none. Let well-named identifiers do the talking. Add a single-line comment only when the *why* is non-obvious (a workaround, a subtle invariant, a hidden constraint). No multi-paragraph docstrings.

### Previews
- Every new composable **must** have a `@PreviewLightDark` preview — write it at the same time you write the composable, not as an afterthought.
- `@PreviewLightDark` (preferred) on stateless `*Content` composables, which are `internal` so previews in the same module can reach them. A handful of older files (`theme/EmptyContent.kt`, `theme/ErrorContent.kt`) still use `@Preview` + `@Preview(uiMode = UI_MODE_NIGHT_YES)` — prefer `@PreviewLightDark` for new code.
- Preview functions are `private` in PascalCase — the Composable naming exemption in `.editorconfig` covers these.
- Shared preview data fixtures live in `utils/PreviewData.kt`.
- When a composable has multiple meaningful states (e.g. empty vs. loaded, default filter vs. active filter), add a separate preview for each — name them `*DefaultPreview`, `*ActivePreview`, etc.

### Test naming
Kotlin backtick syntax with Given/When/Then:
```kotlin
fun `given <context> - when <action> - then <result>`()
```

### ViewModel tests
- Set `Dispatchers.setMain(UnconfinedTestDispatcher())` in `@BeforeTest` so `viewModelScope` runs eagerly.
- Use the shared fakes in `commonTest/` root (`FakeMoviesRepository`, `FakeTvShowsRepository`, `FakeWatchlistRepository`, `FakePersonRepository`, `FakeConfigurationRepository`, `FakeFilterPreferencesStore`) — they live at test-root level and are reused across test files.
- `@AfterTest` always calls `Dispatchers.resetMain()`.
- When a VM depends on use cases, wire the fakes into fresh use-case instances inside each test's `buildViewModel(...)` helper rather than mocking the use case itself.

### UI-model mapping
Map domain → UI model in use cases (see `GetMovieDetailUseCase`) or ViewModels — not in composables. Composables receive ready-to-display data. It's fine for use cases to accept image-URL resolver lambdas (as `GetMovieDetailUseCase` does) because those lambdas resolve against `ConfigurationStore` at mapping time; just don't pass resolver lambdas *down the composable tree*.

### Filter preferences
- `filter/FilterPreferencesStore` persists Movie and TV filter state independently using `multiplatform-settings`. Android uses `SharedPreferencesSettings`; iOS uses `NSUserDefaultsSettings`. Both are bound as `Settings` in their respective `platformModule`.
- `MovieFilterPreferences` and `TvFilterPreferences` are separate typed classes — applying a filter on one tab never affects the other. Each has an `isActive` computed property used to show/hide the badge dot on the filter icon.
- `discoverMovies` / `discoverTvShows` repository methods accept `genreId?`, `sortBy`, `minRating`, `page`. `fetchPage()` routing in each ViewModel: search query wins → filter active → popular fallback.

### Settings preferences (region)
- `settings/SettingsPreferencesStore` exposes `regionCode: StateFlow<String?>` and `suspend setRegionCode(...)`. Backed by the same `Settings` instance as filter prefs. Default is seeded eagerly from `systemRegionCode()` (expect/actual: `Locale.getDefault().country` on Android, `NSLocale.currentLocale.countryCode` on iOS) at construction.
- The store is **injected into `MoviesRepositoryImpl` / `TvShowsRepositoryImpl`**, not threaded through use-case signatures. The repo reads `regionCode.value` synchronously per call and includes it in the `TtlCache` key (so changing region invalidates the right entries). Movies/Shows VMs observe `regionCode.drop(1).distinctUntilChanged()` and trigger `loadData()` on change.
- TMDB endpoints with region wired: `/movie/popular` (`region`), `/discover/movie` (`region`), `/discover/tv` (`watch_region`). `/tv/popular` and `/trending/movie/week` don't accept it. Watch-providers per-title is **not yet** region-wired — extend `getWatchProviders` if/when region needs to influence the "Where to Watch" section.

### Error retry
The movies screen's retry button calls `MoviesViewModel.retry()`, which runs the full `loadData()` (configuration + genres + first page + featuredMovies population). `loadMovies()` / `loadShows()` is the lighter path used by filter changes where config is already loaded. If you add a new initial-load side effect, put it in `loadData()` and retry will pick it up automatically.

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
| multiplatform-settings             | Cross-platform KV persistence (SharedPrefs on Android, NSUserDefaults on iOS) |
| KSP                                | Room annotation processing                                        |
| ktlint-gradle                      | Code style enforcement (wraps ktlint 1.x)                         |
| Kover                              | Code coverage reports (HTML + XML); excludes the `security` package |
| Firebase BOM (Android)             | Pins firebase-appcheck + firebase-appcheck-playintegrity / -debug |
| Firebase iOS SDK (SPM)             | FirebaseCore + FirebaseAppCheck added via Xcode SPM, locked in `Package.resolved` |
| firebase-functions / firebase-admin | Node SDKs used by the Cloud Function (TypeScript, in `functions/`) |
