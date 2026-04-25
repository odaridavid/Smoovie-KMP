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

- [x] `Screen.kt`: `@Serializable TvShowDetailRoute` + `toRoute()` / `toUiModel()`
- [x] `App.kt`: `composable<TvShowDetailRoute>` block + `ShowsScreen` now navigates to it
- [x] `TvShowDetailViewModel` (takes `tvShowId: Int` + `presentLabel: String` params),
      `TvShowDetailUiState` (Loading/Success/Error), `TvShowDetailUiModel` + `SeasonUiModel`
- [x] `shows/data/TvShowDetail.kt` — DTO + `Network` + `Season`. Reuses movie DTOs
      (`Credits`, `VideosResponse`, `ReviewsResponse`) since TMDB returns identical shapes
- [x] `TvShowsRepository.getTvShowDetail(tvShowId)` + impl with `TtlCache` and
      `append_to_response=credits,reviews,videos,recommendations,similar`
- [x] `GetTvShowDetailUseCase` parallel to `GetMovieDetailUseCase` — maps inside via
      `ConfigurationStore` URL builders, takes a `presentLabel` for the year-range suffix
- [x] **`HeroSection` refactored to be media-agnostic**: now takes `backdropUrl: String?`,
      `posterUrl: String?` (instead of `MovieUiModel`) and `onToggleWatchlist: (() -> Unit)?`
      (nullable — hides the toggle if null). `MovieDetailScreen` updated to pass raw URLs;
      `TvShowDetailScreen` passes null for the watchlist callback.
- [x] `CastSection`, `TrailersSection`, `ReviewsSection`, `ShimmerMovieDetail` reused as-is
- [x] Sibling `SimilarTvShowsSection` (duplicates the movies similar pattern, TV-typed) — chose
      duplication over generalization since the two feature packages want to evolve independently
- [x] New `SeasonsSection` composable — horizontal rail of season posters showing
      `Season N`, `2008 · 7 episodes`. Filters out season 0 (specials). No tap action yet.
- [x] Metadata chips: rating (with vote count when loaded), years range (`2008 – 2013` or
      `2008 – present`), `5 seasons · 62 episodes`, genres, networks (with 📺 prefix)
- [x] `FakeTvShowsRepository` gained `tvShowDetail` var + `getTvShowDetail` impl
- [x] `TvShowDetailViewModelTest` — 6 cases (success mapping, in-production year range,
      specials filtered, singular labels for 1-season shows, network error, retry success)
- [x] Koin: `GetTvShowDetailUseCase` + `viewModel { (id: Int, presentLabel: String) -> ... }`
      with `koinViewModel(key = "tv_${id}", parameters = { parametersOf(id, presentLabel) })`
- [x] Strings: `tv_seasons_section_title`, `tv_show_year_range_present`,
      `error_tv_show_detail_failed`

**Definition of done:** full TV detail screen shippable; users can read cast, watch trailers,
see seasons list but not drill in.
✅ **Done at code level.** Android + iOS compile, all unit tests pass (including the 6 new
TvShowDetail tests). Not yet verified in a running emulator.

Notes for Phase 4 (episodes):

- `SeasonsSection` currently has no tap action. Phase 4 gives each season a click handler
  that pushes into an episodes screen.
- TMDB endpoint: `/tv/{tv_id}/season/{season_number}` returns full episode details.
- Ideally a new `TvSeasonDetailRoute` with (tvShowId, seasonNumber, seasonName) — the season
  name is already in the route payload from `SeasonUiModel`.

## Phase 4 — Episodes drill-in

**Goal:** tapping a season on the TV show detail opens a season screen showing all episodes
with metadata and overviews. Ships the final piece of the core TV browsing story.

- [x] `Screen.kt`: `@Serializable TvSeasonDetailRoute(tvShowId, seasonNumber, seasonName)`
- [x] `App.kt`: `composable<TvSeasonDetailRoute>` block
- [x] `shows/data/SeasonDetail.kt` DTO — `SeasonDetail` + `Episode`
      (`still_path`, `episode_number`, `air_date`, `runtime`, `vote_average`)
- [x] `TvShowsRepository.getSeasonDetail(tvShowId, seasonNumber)` + impl with a
      `(tvShowId, seasonNumber)` keyed `TtlCache`
- [x] `GetSeasonDetailUseCase` — maps episode stills via `backdropUrl(BackdropSize.SMALL)`
      since TMDB episode stills share the backdrop CDN path
- [x] `SeasonDetailUiModel` + `EpisodeUiModel` + mapper:
      - Episodes sorted by `episodeNumber`
      - `headerLabel` is `"Ep N · Name"` (drops the "· Name" if blank)
      - `episodeCountLabel` respects singular/plural ("1 episode" vs "N episodes")
      - `runtimeLabel` is `"N min"` or blank if runtime is null
- [x] `SeasonDetailViewModel` parametrized by `(tvShowId, seasonNumber)`
- [x] `SeasonDetailScreen` — `CenterAlignedTopAppBar` with back + season name, scrollable list
      with season overview header and episode cards
- [x] `components/EpisodeItem` composable — still image, header label, date · runtime metadata,
      rating, expandable overview (reuses `ExpandableText`)
- [x] `SeasonsSection` items now tappable via `onSeasonClick: (SeasonUiModel) -> Unit` default
- [x] `SeasonUiModel` gained `seasonNumber` field (required for the TMDB query)
- [x] `TvShowDetailScreen` takes an `onSeasonClick` param, threads through `App.kt` which
      builds a `TvSeasonDetailRoute` from `(route.id, season.seasonNumber, season.name)`
- [x] Koin: `GetSeasonDetailUseCase` single +
      `viewModel { (tvShowId: Int, seasonNumber: Int) -> SeasonDetailViewModel(...) }`
- [x] Strings: `error_season_detail_failed`
- [x] `FakeTvShowsRepository.seasonDetail` + `getSeasonDetail` impl
- [x] `SeasonDetailViewModelTest` — 9 cases (success, sort order, header label with/without
      name, singular episode label, runtime with/without, network error, retry)

**Definition of done:** episodes drill-in is live — tap a season poster → season screen with
episode cards → tap "Read more" on long overviews → expand inline.
✅ **Done at code level.** Android + iOS compile, all unit tests pass. Not yet verified in a
running emulator.

## Deferred / parallel work
- [ ] **Watchlist TV support** — Room migration: add `mediaType` column to
      `WatchlistMovieEntity`. Filter chip on watchlist screen. Can ship after Phase 3.
- [x] **Person filmography TV credits** — `append_to_response=movie_credits,tv_credits` on
      `/person/{id}`. `PersonDetail` DTO gained `tvCredits: TvCredits?` + `PersonTvCredit`.
      `PersonDetailUiModel` exposes two parallel lists: `movieFilmography:
      List<PersonMovieFilmographyItem>` and `tvFilmography: List<PersonTvFilmographyItem>`
      (the earlier sealed-merge approach was rejected in favour of two separate rails).
      `PersonDetailScreen` now renders **two horizontal poster rails** — Movies + TV Shows —
      each capped at 6 with a "View all" button when more credits exist. Tapping "View all"
      pushes a new `PersonFilmographyRoute(personId, personName, mediaType)` →
      `PersonFilmographyScreen` (single screen branching on `PersonFilmographyMediaType`)
      that shows the full vertical list with `MovieCard` for movies / `TvShowCard` + `TvBadge`
      for TV. New `PersonFilmographyViewModel` shares the same `GetPersonDetailUseCase` so
      `TtlCache` makes the second load a hit. Cap of 20 was removed since the View-All screen
      shows the full list. Sibling `MovieFilmographyRail` + `TvShowFilmographyRail` composables
      under `person/components/`, plus a shared `RailHeader` that conditionally renders the
      "View all" `TextButton`. New string `action_view_all`. Tests updated
      (`PersonDetailViewModelTest` + `PersonDetailUiModelTest`) for the two separate lists
      with new cases for TV-only filmography and id-collision-doesn't-collide.
- [x] **Featured pager with TV content** — `shows/components/FeaturedTvShowsPager` mirrors
      `FeaturedMoviesPager` (TV-typed). `ShowsScreenState.featuredTvShows` populated by
      `ShowsViewModel.loadData()` from the first popular page (same pattern as movies).
      `ShowsScreen` restructured to match `MoviesScreen`'s top-zone layout: pager at the top
      (empty topBar), genre chips below the pager, list below; when no featured is loaded,
      `CenterAlignedTopAppBar` renders in the topBar as before. `ShimmerFeaturedSection` from
      `theme/` reused during loading. Genre switches keep featured stable (tracked by a test).
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
