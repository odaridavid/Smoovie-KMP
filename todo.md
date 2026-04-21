# Smoovie — Enhancement Ideas

## Search & Discovery

- [ ] Search history — save recent queries locally so users can quickly re-search

## Offline & Performance

- [ ] Local caching with a database (SQLDelight / Room KMP) so the app works offline

## UI Polish

- [ ] Collapsing header with the backdrop of a featured/trending movie
- [ ] Error ui could look better

## Backend & Services

- [ ] Mock backend for movie ticketing/seat allocation system (showtimes, seat maps, booking flow,
  ticket confirmation)

## Architecture & Quality

- [ ] Improve error handling — more granular error types (network vs server vs parsing),
  user-friendly messages, retry strategies
- [ ] Compose UI tests for the screens (screenshot tests or interaction tests)
- [ ] Error retry per-search-query (currently retry always reloads popular movies)
- Clean files and mapping
- Shared transition to person details
- Improve navigation
- Add github ci/cd
- Improve the claude.md and use skills