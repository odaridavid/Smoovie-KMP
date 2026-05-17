# App Store Listing — Smoovie

Source of truth for the App Store Connect listing. Update in the same PR as behaviour changes that affect what users see.

The values here are submitted via App Store Connect (no API in CI) — copy them in manually when preparing a release.

## App information

| Field | Value | Notes |
|---|---|---|
| App name | Smoovie | 30-char limit. Used on the home screen. |
| Subtitle | _TODO_ — e.g. "Discover movies and TV" | 30-char limit. Shown under the name in search. |
| Bundle ID | `dev.odaridavid.smoovie` | Matches `PRODUCT_BUNDLE_IDENTIFIER` in `Configuration/Config.xcconfig`. |
| Primary category | Entertainment | |
| Secondary category | Reference | Optional; surfaces in the Reference category browse. |
| Age rating | 12+ | TMDB surfaces reviews and content descriptors that can include violence/profanity. Confirm via App Store Connect's questionnaire. |
| Content rights | Does not contain, show, or access third-party content. **Untick** — TMDB content is third-party. | App Review will reject if this is set wrong. |

## URLs

| Field | URL |
|---|---|
| Privacy Policy URL | https://smoovie-kmp.web.app/privacy |
| Support URL | _TODO_ — recommended: https://github.com/odaridavid/Smoovie-KMP/issues |
| Marketing URL (optional) | _TODO_ |

## Listing copy

### Promotional text (170 chars, editable without resubmission)
> _TODO_ — e.g. "Browse popular movies and TV, search the TMDB catalog, see where to watch, and bookmark titles to a local watchlist."

### Description (4000 chars)

_TODO_ — paste the long-form description here. Suggested skeleton:

> Smoovie is a free movie and TV discovery app powered by The Movie Database (TMDB). Browse popular titles, search across films and shows, drill into cast and trailers, see where you can stream a title in your region, and bookmark anything to a local watchlist that lives on your device.
>
> Features
> • Popular and trending movies and TV shows
> • Search the TMDB catalogue
> • Cast, trailers, reviews, seasons, and watch providers
> • Region picker — see streaming availability in your country
> • Local watchlist, stored entirely on-device
>
> No account, no ads, no tracking. Crash reporting is opt-in via the Settings tab.
>
> Smoovie uses the TMDB API but is not endorsed or certified by TMDB.

### Keywords (100 chars, comma-separated)

_TODO_ — e.g. `movies,tv,shows,watchlist,trailers,cast,streaming,tmdb,cinema,reviews`

### Copyright

`© 2026 David Odari Kiribwa`

## App Privacy (privacy nutrition labels)

Filled in App Store Connect → **App Information → App Privacy**. Mirror the Play Data Safety entries.

### Data Used to Track You

**None.** Smoovie does not use third-party SDKs for advertising, attribution, or cross-app tracking.

### Data Linked to You

**None.** The app has no accounts, no email collection, no analytics events.

### Data Not Linked to You

| Category | Data type | Used for | Notes |
|---|---|---|---|
| Diagnostics | Crash data | App functionality | Firebase Crashlytics, opt-in via the Settings "Send crash reports" toggle. Default off. |
| Diagnostics | Performance data | App functionality | Only if Crashlytics consent is granted; no Firebase Performance SDK. |

App Check (App Attest) generates a device-attestation token managed by iOS itself and validated by Firebase. It is not user-identifiable and is not exposed to app code. Per Apple's privacy guidance, this does not require a Privacy Nutrition Label entry.

## Assets

### App icon

- 1024 × 1024 PNG, no transparency, no rounded corners.
- Lives at `iosApp/iosApp/Assets.xcassets/AppIcon.appiconset/` — App Store Connect picks it up from the built archive.

### Screenshots

Apple now only requires the **largest** size for each device family — other sizes auto-scale. Required:

| Device | Size (px) | Source device |
|---|---|---|
| iPhone 6.9" | 1320 × 2868 (portrait) | iPhone 16 Pro Max simulator |
| iPad 13" | 2064 × 2752 (portrait) | iPad Pro 13" (M4) simulator |

Minimum 3 screenshots per device family, maximum 10. Captured from a clean simulator with status-bar tidied (use `xcrun simctl status_bar override` to force a clean time/battery/signal).

Suggested set (matches Play Store):
1. Popular movies grid
2. Movie detail screen (cast + trailer thumbnail visible)
3. Search results
4. Watchlist
5. Settings (region picker)

### App preview videos (optional)

15–30 sec `.mov` or `.mp4`, captured from a real device or simulator. Skip for v1.

## TestFlight

| Field | Value |
|---|---|
| Beta App Description | _TODO_ |
| Feedback Email | _TODO_ |
| Marketing URL | _TODO_ |
| Privacy Policy URL | https://smoovie-kmp.web.app/privacy |

Internal testing (up to 100 testers) skips Beta App Review. External testing groups require Beta App Review on the first build only.

## Submission checklist

Before tapping **Submit for Review**:

- [ ] Build uploaded to TestFlight via the `release-testflight.yml` workflow (or manual archive).
- [ ] App tested on a real device via TestFlight Internal — App Attest verified end-to-end (movies and shows load).
- [ ] Privacy Nutrition Labels filled out.
- [ ] Screenshots uploaded for both device families.
- [ ] Description, keywords, promotional text reviewed and proofread.
- [ ] Demo account fields: **leave blank** — Smoovie has no login.
- [ ] App Review notes: mention that the app uses Firebase App Check (App Attest) and proxies the TMDB API via a Cloud Function. No login required.
- [ ] Content rights box ticked correctly (third-party content from TMDB).
- [ ] Privacy Policy URL responds with 200 (verify https://smoovie-kmp.web.app/privacy renders).
