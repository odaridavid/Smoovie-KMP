# Smoovie — Enhancement Ideas

## Search & Discovery

- [ ] Search history — save recent queries locally so users can quickly re-search
- [ ] Genre filtering / chips above the movie list

## Movie Details

- [ ] Chip variety — use distinct chip styles/icons for metadata (e.g. filled chip for rating, outlined for genres, icon chips for runtime/director)

## Offline & Performance

- [ ] Local caching with a database (SQLDelight / Room KMP) so the app works offline
- [ ] Image preloading for smoother scrolling

## UI Polish

- [ ] Pull-to-refresh on the movie list
- [ ] Animated card entrance (staggered fade/slide as cards appear)
- [ ] Collapsing header with the backdrop of a featured/trending movie

## Backend & Services

- [ ] Mock backend for movie ticketing/seat allocation system (showtimes, seat maps, booking flow, ticket confirmation)

## Architecture & Quality

- [ ] Improve error handling — more granular error types (network vs server vs parsing), user-friendly messages, retry strategies
- [ ] Pagination state in the ViewModel (loading-more indicator at the bottom of the list)
- [ ] Compose UI tests for the screens (screenshot tests or interaction tests)
- [ ] Error retry per-search-query (currently retry always reloads popular movies)