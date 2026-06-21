# Habit Tracker

A minimalist Android habit tracking app built around a simple conviction: before optimizing a habit, you first need to establish it.

Most habit trackers punish inconsistency. This one doesn't. There are no streaks to break, no scores to lose, no gamification layer pushing you toward a number. Instead, the app focuses on making the act of showing up as low-friction as possible — because consistency over time matters more than perfect daily performance.

## Features

- **Google Sign-In** — authentication via Firebase Auth
- **Habit management** — create and store habits with flexible goals and preferences
- **Daily check-in** — register each day as `YES`, `NO`, or `LATER` with minimal interaction
- **Persistent event log** — all check-ins stored in Cloud Firestore and recovered correctly after app restart
- **Smart notifications** — grouped daily reminders (max 3/day at 8:00, 14:00, 20:00) via AlarmManager, with background data generation handled by WorkManager
- **Philosophy screen** — shown once on first launch via SharedPreferences, explaining the app's approach to habit building

## Tech stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose |
| Architecture | MVVM with shared ViewModel |
| Database | Cloud Firestore (`users/{userId}/habits`, `users/{userId}/events`) |
| Auth | Firebase Authentication (Google Sign-In) |
| Background work | WorkManager (nightly NO_DATA generation) |
| Notifications | AlarmManager |
| Navigation | NavHost |
| State management | StateFlow + Coroutines |

## Project structure

```
app/
├── data/          # models, repositories, Firestore integration
├── ui/            # Compose screens and components
└── docs/          # project documentation
```

## Status

Core functionality is complete and verified end to end — authentication, habit creation, daily check-ins, event persistence, notifications, and background processing all working as expected. Active development ongoing.
