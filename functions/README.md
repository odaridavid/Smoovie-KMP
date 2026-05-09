# Smoovie TMDB Proxy

Thin Cloud Function that holds the TMDB API token server-side so it never ships in
the mobile app binaries. The function only accepts requests carrying a valid
Firebase App Check token, attaches the bearer header, and forwards `GET /3/*`
calls to `api.themoviedb.org`.

## One-time setup

```sh
# from the repo root
npm install -g firebase-tools
firebase login
cd functions
npm install
```

## Set the TMDB secret

```sh
firebase functions:secrets:set TMDB_TOKEN
# paste your TMDB API Read Access Token when prompted
```

The secret is stored in Google Secret Manager and only resolved at function
runtime. It never lives in code or in the repo.

## Deploy

```sh
firebase deploy --only functions
```

Note the printed function URL — something like
`https://tmdbproxy-<hash>-ew.a.run.app`. The mobile app's `TMDB_BASE_URL` will
point at this in Phase 3.

## Local development

```sh
npm run serve
# emulator runs at http://127.0.0.1:5001/smoovie-kmp/europe-west1/tmdbProxy
```

App Check enforcement is **disabled in the emulator**, so you can hit the
emulated function directly. In production, requests without a valid App Check
token are rejected with HTTP 401.

## Tail logs

```sh
firebase functions:log
```
