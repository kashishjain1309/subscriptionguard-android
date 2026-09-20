# SubscriptionGuard — Android

Native Android client for SubscriptionGuard, a subscription management app that tracks recurring expenses, flags renewals before they happen, and reconciles subscriptions once a billing cycle ends. This app is the frontend for the companion Spring Boot API ([SubscriptionGuard Backend](https://github.com/kashishjain1309/subscriptionguard-backend)) — see that repo for the server-side architecture, reconciliation engine, and API details.

## Features

- Google Sign-In via Android Credential Manager, no password entry
- Dashboard showing active subscriptions with next billing date, cost, and category, with soon-to-renew subscriptions visually flagged
- Add, edit, and cancel subscriptions, supporting auto-renewing, fixed-term, and manual billing types
- A reconciliation dialog that automatically appears when a subscription's billing cycle has ended, with wording and actions that adapt to the subscription's type
- Push notifications for upcoming renewals, handled in both foreground and background app states
- Automatic access token refresh on expiry, transparent to the rest of the app
- Profile screen with logout

## Tech Stack

Kotlin, Jetpack Compose, Retrofit, OkHttp, Jetpack DataStore, Firebase Cloud Messaging, Android Credential Manager, Navigation Compose

## Architecture

MVVM-style structure: Composable screens hold no logic of their own and only read state from a `ViewModel`, which in turn calls a `Repository`. Repositories wrap Retrofit calls and return a `Result`, keeping error handling in one place rather than scattered across the UI. Networking includes an `AuthInterceptor` that attaches the stored access token to every request, and a `TokenAuthenticator` that transparently refreshes an expired token and retries the original request — using a separate, unauthenticated HTTP client for the refresh call itself, to avoid the client re-triggering its own token logic.

## Screens

Splash → Login → Home (dashboard) → Add/Edit Subscription → Subscription Detail → Profile, plus a reconciliation dialog that can appear over Home.

## Running Locally

**Prerequisites**: Android Studio, a running instance of the [backend](https://github.com/kashishjain1309/subscriptionguard-backend), a Firebase project linked to this app's package name (`com.kashish.subscriptionguard`), a Google OAuth Web Client ID (shared with the backend) and a matching Android OAuth Client (registered with your debug keystore's SHA-1).

1. Clone the repo
2. Copy `app/google-services.json.example` to `app/google-services.json` and fill in your own Firebase project values
3. In `app/build.gradle.kts`, set `BASE_URL` and `WEB_CLIENT_ID` in `buildConfigField` to point at your running backend and your Google OAuth Web Client ID
4. If testing on the emulator, `BASE_URL` should be `http://10.0.2.2:PORT/`, which resolves to your host machine
5. Sync Gradle and run