# Smoovie — Enhancement Ideas

## Features

- [ ] Auth login/logout
- [ ] Settings/Profile

### External content sources

- [ ] Cross-source rating chips on movie detail — OMDb free tier, keyed by IMDb ID (need to add
  `imdb_id` back to the `MovieDetail` DTO); gives Rotten Tomatoes + Metacritic in one call
- [ ] Tracking + social features (watched/watching lists, episode progress for TV, social recs)
  — Trakt.tv OAuth API; complements rather than replaces TMDB
- [ ] High-res artwork on hero sections (clearlogos, character art, banners) — Fanart.tv free
  API with a key; noticeable UI polish over TMDB's backdrop-only assets
- [ ] Soundtrack section on movie detail — Spotify Web API; deep-link out or embed a preview
  player
- [ ] Trivia panels (awards, filming locations, based-on-which-book, box office) — Wikidata
  SPARQL or REST, no auth required

## Backend & Services

- [ ] Mock backend for movie ticketing/seat allocation system (showtimes, seat maps, booking flow,
  ticket confirmation)
    - **Why this over a public API:** Kinoheld's GraphQL has zero Düsseldorf cinemas (UCI /
      Cinestar / Black Box / Bambi / Atelier / Cinema Düsseldorf / Metropol all absent; nearest
      is Studiokino Ratingen ~9.5km out). Movieglu has coverage but is paid. Building a mock
      gives full control over seat-allocation/booking flows that public APIs don't expose anyway.
    - **Shape:** new `server/` Gradle module in this repo (shares DTOs with `commonMain` via a
      `shared-api` module or a plain `kotlinx.serialization` package).
    - **Stack:** Ktor server + kotlinx.serialization. Start with in-memory storage (maps keyed
      by id, seeded fixture), swap to Exposed + H2 when persistence is needed.
    - **v1 endpoints:**
        - `GET /movies/{tmdbId}/showtimes` — showtimes list for a given TMDB movie id
        - `GET /showtimes/{id}` — showtime detail incl. seat grid (rows × columns, each seat:
          `AVAILABLE` / `HELD` / `BOOKED`)
        - `POST /showtimes/{id}/reservations` — hold N seats for M minutes, returns reservation
          id + expiry
        - `POST /reservations/{id}/confirm` — turn hold into booking, returns ticket
        - `DELETE /reservations/{id}` — release hold early
    - **Auth:** `X-User-Id` header (dumb session) for v1; JWT/OAuth later.
    - **Android/iOS wiring:** `http://10.0.2.2:PORT` for Android emulator, `http://localhost:PORT`
      for iOS sim. Expose base URL via `expect val serverBaseUrl` or BuildConfig/xcconfig like
      the TMDB token.
    - **Open questions to resolve when unparking:** deployment target (local only, or Fly.io
      / Railway / Render for a live demo?); Stripe test-mode payments yes/no; admin UI for
      seeding showtimes yes/no.

## Architecture & Quality

- [ ] Compose UI tests for the screens (screenshot tests or interaction tests)
- [ ] AI and app actions
- [ ] Multimodules
- [ ] Observability - https://github.com/AAkira/Napier
- [ ] Update readme before going public with it
- [ ] Single vs multi select for genres
