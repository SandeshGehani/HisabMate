# HisabMate - Personal Expense Tracker

A simple, offline-first Android app for tracking daily meals, teas, and money contributions in shared living situations.

## 📱 Features

- **Daily Tracking**: Log meals, teas, and money contributions
- **Calendar View**: Visual monthly calendar with daily indicators
- **Streak System**: Track consecutive daily entries
- **Monthly Badges**: Earn badges for completing all days
- **Smart Reminders**: Daily notifications at 9 PM if entry is missing
- **Month-End Summary**: Calculate costs with custom rates
- **Offline-First**: All data stored locally with Room Database

## 🏗️ Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose (Material 3)
- **Architecture**: MVVM
- **Database**: Room (SQLite)
- **Background**: WorkManager
- **Navigation**: Navigation Compose
- **State**: StateFlow/Flow

## 📸 Screenshots

*(Add screenshots here after building)*

## 🚀 Getting Started

### Prerequisites

- Android Studio Hedgehog or newer
- JDK 17 or higher
- Android SDK 34
- Minimum Android version: 7.0 (API 24)

### Download & Installation

1. **Download APK**: You can download the latest APK from the [Releases section](https://github.com/SandeshGehani/HisabMate/releases) (if available) or build it manually.
2. **Install**: Enable "Install from Unknown Sources" on your Android device and open the APK.

### Building the Project

1. Clone the repository:
```bash
git clone https://github.com/SandeshGehani/HisabMate.git
cd HisabMate
```

2. Open in Android Studio

3. Sync Gradle dependencies

4. Run on emulator or device

### Building APK

```bash
./gradlew assembleDebug
# or for release
./gradlew assembleRelease
```

APK location: `app/build/outputs/apk/`

## 📁 Project Structure

```
app/
├── data/
│   ├── local/
│   │   ├── entities/      # Room entities
│   │   ├── dao/           # Data Access Objects
│   │   └── HisabMateDatabase.kt
│   └── repository/        # Repository pattern
├── ui/
│   ├── screens/           # Composable screens
│   └── theme/             # Material 3 theme
├── viewmodel/             # ViewModels (MVVM)
├── navigation/            # Navigation setup
├── utils/                 # Utility classes
└── worker/                # Background workers
```

## 🎯 Core Functionality

### Screens

1. **Onboarding** - First-time user introduction (3 screens)
2. **Home Dashboard** - Current month overview with quick actions
3. **Add/Edit Record** - Input meals, teas, and money
4. **Calendar** - Monthly view with visual indicators
5. **Month-End Summary** - Calculate and save monthly costs
6. **Streaks & Badges** - View achievements and progress

### Database Schema

- **DailyRecord**: date, mealsCount, teasCount, moneyAmount
- **MonthlySummary**: month, year, totals, rates, badgeEarned
- **Streak**: currentStreak, bestStreak, lastRecordedDate

## 🔔 Notifications

- Daily reminder at 9:00 PM
- Only triggers if today's record is missing
- Requires POST_NOTIFICATIONS permission (Android 13+)

## 🏆 Gamification

- **Streaks**: Track consecutive daily entries
- **Badges**: Earn monthly badges for logging all days
- **Statistics**: View best streak, total entries, consistency

## 🛠️ Development

### Key Dependencies

```kotlin
// Jetpack Compose
implementation("androidx.compose.material3:material3")
implementation("androidx.navigation:navigation-compose:2.7.7")

// Room Database
implementation("androidx.room:room-runtime:2.6.1")
implementation("androidx.room:room-ktx:2.6.1")
ksp("androidx.room:room-compiler:2.6.1")

// WorkManager
implementation("androidx.work:work-runtime-ktx:2.9.0")
```

### Architecture

- **MVVM Pattern**: Separation of concerns
- **Repository Pattern**: Data abstraction
- **Flow/StateFlow**: Reactive data streams
- **Dependency Injection**: ViewModelFactory

## 📝 License

This project is open source and available under the MIT License.

## 👤 Author

**Sandesh**

## 🤝 Contributing

Contributions, issues, and feature requests are welcome!

## ⭐ Show your support

Give a ⭐️ if this project helped you!

---

**Note**: This is a personal project designed for single-user, offline usage. Cloud sync and multi-user features are not currently supported.
