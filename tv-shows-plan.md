# TV Shows Feature — Working Plan

Living document for the TV shows feature. Delete once the feature merges and the notes are
subsumed by code / commit history.

## Approach at a glance

- **Parallel feature package** (`tv/`) mirroring `movies/`, with shared UI components.
- **Entry point:** Material3 `NavigationBar` (bottom nav) with three tabs: `Movies`, `Shows`,
  `Watchlist`. Started with two (Movies + Shows); Watchlist promoted to the bottom bar as a
  follow-up migration before Phase 2 — the bookmark icon and back-arrow were removed from the
  hero pager / shimmer / collapsed toolbar since the tab now owns the entry point.
- **Episodes deferred** to a later phase — Phase 3 ships with a seasons rail only.

---

## Phase 1 — Entry point (no TV data yet)

**Goal:** app has two bottom-bar tabs; tapping Shows navigates to a placeholder empty state.
No new APIs called. Ships in isolation, unblocks Phase 2.

- [x] Add `ShowsRoute` as a `@Serializable data object` in `Screen.kt`
- [x] Create `shows/ShowsScreen.kt` — placeholder composable rendering `EmptyContent` with
      `Icons.Default.Tv` ("TV shows coming soon"), with `statusBarsPadding`
- [x] Refactor `App.kt` to wrap the `NavHost` in a `Scaffold` with a `NavigationBar`
      bottom bar containing Movies + Shows tabs
- [x] Bottom bar visibility: only shown on top-level tab destinations
      (Movies, Shows). Hidden on `MovieDetailRoute` / `PersonDetailRoute` / `WatchlistRoute`.
- [x] Tab navigation pattern: `popUpTo(startDestination) { saveState = true } +
      launchSingleTop + restoreState` so state (scroll, filters, loaded pages) persists
      across tab switches
- [x] String resources: `media_type_movies`, `media_type_tv_shows` (used as bottom-bar labels),
      `tv_shows_coming_soon` (Shows screen empty state)
- [x] Delete the superseded segmented-toggle code (`MediaType.kt`, `MediaTypeToggle.kt`),
      remove `selectedMediaType` from `MoviesScreenState`, `onMediaTypeSelected` from
      `MoviesViewModel`, and the associated 3 unit tests
- [x] Compile check (Android + iOS + commonMain) and existing tests still pass

**Definition of done:** bottom bar shows Movies + Shows, tapping switches tabs, movies side
still works, Shows shows the coming-soon state, state persists on tab switch.
✅ **Done at code level.** Not yet verified in a running app — `./gradlew installDebug` + an
emulator run outstanding before declaring shippable. Known follow-ups:

- ~~Tab switches currently use the NavHost's default slide-horizontal transition~~ **Resolved:**
  per-destination `EnterTransition.None` / `ExitTransition.None` overrides on
  `composable<MoviesRoute>` / `composable<ShowsRoute>` / `composable<WatchlistRoute>` when
  the other side of the transition is also a top-level tab. Detail pushes keep the slide.
- ~~Watchlist still reached via bookmark icon on the hero pager~~ **Resolved:** promoted to a
  3rd bottom-bar tab. Bookmark icon + back-arrow removed from hero / shimmer / collapsed
  toolbar / watchlist screen. The movie-detail hero's bookmark (toggle watchlist for current
  movie) stays — that's a different affordance.

## Phase 2 — TV shows lists & browse

**Goal:** TV tab looks and feels identical to Movies — popular grid, genre chips, search,
pagination.

- [x] New `shows/data/` package: `TvShowsRepositoryImpl`, DTOs (`TvShow`, `TvShowsResponse`,
      `TvGenre`, `TvGenresResponse`), reuses the existing Ktor client
- [x] TMDB endpoints: `/tv/popular`, `/search/tv`, `/genre/tv/list`, `/discover/tv`
- [x] New `shows/domain/` use cases: `GetPopularTvShowsUseCase`, `SearchTvShowsUseCase`,
      `GetTvShowsByGenreUseCase`, `GetTvGenresUseCase`, plus `TvShowsPage` model and
      `TvShowsRepository` interface
- [x] `TvShowUiModel`, `TvGenreUiModel`, `TvShowUiMapper` (reuses movies' `toReadableDate` /
      `toDisplayRating` extensions — internal module visibility)
- [x] Decision resolved: **separate `ShowsViewModel`** (not extending `MoviesViewModel`), since
      bottom-nav made each tab its own destination with its own VM — the original
      extend-vs-split question became moot
- [x] Genre chips: sibling `TvGenreChips` composable (mirrors `GenreChips`) — TV genres differ
      from movies so they're a distinct list
- [x] Sibling `TvShowCard` + `tvShowItems` list builder — duplicates movies' pattern; extract
      a shared `MediaCard` later if duplication becomes painful
- [x] Featured pager omitted on Shows tab (deferred per plan)
- [x] `ShimmerMovieList(showHero = false)` reused for loading state (it's generic skeleton
      cards — no movie-specific refs)
- [x] `SearchToolbar` parameterised with a `placeholder: String` default; added
      `search_shows_hint` string
- [x] Koin wiring: `TvShowsRepository` / `TvShowUiMapper` / four use cases / `ShowsViewModel`
- [x] `App.kt` `composable<ShowsRoute>` now resolves `ShowsViewModel` via `koinViewModel()`
- [x] `FakeTvShowsRepository` + `ShowsViewModelTest` with 9 cases (success / empty / error /
      retry / genre select / genre no-op / genres failure isolation / genres populated /
      pagination append)

**Definition of done:** browse, search, filter-by-genre TV shows works; tapping a card
navigates nowhere yet (no detail screen).
✅ **Done at code level.** Android + iOS compile, all unit tests pass. Not yet verified in
a running emulator — `./gradlew installDebug` outstanding.

Notes for future phases:

- Tapping a card currently no-ops (`onTvShowClick = {}` default). Phase 3 wires it to a
  `TvShowDetailRoute`.
- `MoviesViewModel` and `ShowsViewModel` both call `loadConfiguration()` in their init. If
  the user opens Shows first, config loads there; switching to Movies re-calls
  `loadConfiguration()`. Acceptable minor cost; could be hoisted to an App-level bootstrap
  later.
- Pattern borrowed from movies one-to-one. If we change e.g. the pagination behavior on
  movies, mirror it here.

## Phase 3 — TV show detail + seasons rail

**Goal:** tapping a TV show card opens a detail screen with cast, trailers, reviews, similar,
and a seasons rail. Episodes still deferred.

- [ ] `Screen.kt`: add `@Serializable TvShowDetailRoute` + `toRoute()` / `toUiModel()`
- [ ] `App.kt`: add `composable<TvShowDetailRoute>` block
- [ ] `TvShowDetailViewModel`, `TvShowDetailUiState`, `TvShowDetailUiModel`
- [ ] `tv/data/TvShowDetail.kt` DTO with `seasons`, `numberOfSeasons`, `numberOfEpisodes`,
      `firstAirDate`, `lastAirDate`, `inProduction`, `networks`
- [ ] `GetTvShowDetailUseCase` parallel to `GetMovieDetailUseCase`
- [ ] `TvShowDetailScreen` reuses `HeroSection`, `CastSection`, `TrailersSection`,
      `ReviewsSection`, similar rail. Decide whether to rename `SimilarMoviesSection` →
      `SimilarMediaSection` or keep both.
- [ ] New `SeasonsSection` composable — horizontal rail of season posters, each showing
      `Season N · X episodes · year`. No tap action yet.
- [ ] Metadata chips adapt: `5 seasons · 62 episodes`, `2019 – present`, networks

**Definition of done:** full TV detail screen shippable; users can read cast, watch trailers,
see seasons list but not drill in.

## Deferred / parallel work

- [ ] **Episodes drill-in** — Phase 4. `/tv/{id}/season/{n}` + new episodes screen.
- [ ] **Watchlist TV support** — Room migration: add `mediaType` column to
      `WatchlistMovieEntity`. Filter chip on watchlist screen. Can ship after Phase 3.
- [ ] **Person filmography TV credits** — `append_to_response=tv_credits` on `/person/{id}`,
      merge into filmography rail with a `TV` badge. Independent, can ship anytime after
      Phase 2.
- [ ] **Featured pager with TV content** — trivial after Phase 2; toggle-aware data source.
- [ ] **`/search/multi` unified search** — optional during Phase 2 if mixed results feel
      better than per-tab search.

## Open decisions

These will be resolved during implementation; tracked here so we don't lose them.

- VM shape for Phase 2: extend `MoviesViewModel` vs split into `HomeViewModel`?
- `SimilarMoviesSection`: rename to `SimilarMediaSection` or fork?
- ~~Segmented toggle placement: above `SearchToolbar` or below it?~~ **Superseded:** scrapped
  segmented toggle in favour of a Material3 `NavigationBar` at the bottom. Rationale: the
  toggle consumed vertical space in the content area on every home render; bottom nav puts
  the chrome in the standard place and scales to more tabs (Watchlist follow-up).
- Tab-switch transition animation (currently slide-horizontal default) — fine for Phase 1,
  revisit if it feels wrong on device.
- `MediaCard`: unify or keep separate `MovieCard` / `TvShowCard`?

## Notes

_Scratchpad for findings during implementation. Delete before final merge._
