# Smoovie — Enhancement Ideas

## Search & Discovery

- [ ] Pagination — the API returns `totalPages` but only the first page is loaded; add infinite scroll
- [ ] Search history — save recent queries locally so users can quickly re-search
- [ ] Genre filtering / chips above the movie list

## Movie Details

- [ ] Detail screen — tap a card to see full overview, backdrop image, cast, etc. (`backdropUrl` is already mapped but unused in the UI)
- [ ] Trailers — TMDB has a videos endpoint

## Offline & Performance

- [ ] Local caching with a database (SQLDelight / Room KMP) so the app works offline
- [ ] Image preloading for smoother scrolling

## UI Polish

- [ ] Pull-to-refresh on the movie list
- [ ] Animated card entrance (staggered fade/slide as cards appear)
- [ ] Collapsing header with the backdrop of a featured/trending movie

## Architecture & Quality

- [ ] Pagination state in the ViewModel (loading-more indicator at the bottom of the list)
- [ ] Compose UI tests for the screens (screenshot tests or interaction tests)
- [ ] Error retry per-search-query (currently retry always reloads popular movies)