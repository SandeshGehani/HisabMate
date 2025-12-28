# 🏗️ Building HisabMate APK

## Quick Start (For Mobile Installation)

### Option 1: Build in Android Studio (Recommended)

1. **Open Project**
   - Open Android Studio
   - File → Open → Select `hisabMate` folder
   - Wait for Gradle sync to complete

2. **Build APK**
   - Build → Build Bundle(s) / APK(s) → Build APK(s)
   - Wait for build to complete (2-5 minutes)
   - Click "locate" in the notification to find the APK

3. **Install on Phone**
   - Transfer APK to your phone via USB/Bluetooth/Drive
   - Enable "Install from Unknown Sources" in phone settings
   - Tap the APK file to install

### Option 2: Command Line Build

If you have Gradle installed:

```bash
# Windows
gradlew.bat assembleDebug

# Linux/Mac
./gradlew assembleDebug
```

APK Location: `app/build/outputs/apk/debug/app-debug.apk`

### Option 3: Download Pre-built APK

*(Will be available in GitHub Releases after first build)*

## 📱 Installation Steps

1. **Transfer APK to Phone**
   - USB cable
   - Google Drive/Dropbox
   - Email to yourself
   - Bluetooth

2. **Enable Unknown Sources**
   - Settings → Security → Unknown Sources (ON)
   - Or: Settings → Apps → Special Access → Install Unknown Apps

3. **Install**
   - Open file manager
   - Navigate to APK location
   - Tap to install
   - Grant permissions when asked

4. **First Launch**
   - Complete 3-screen onboarding
   - Grant notification permission (for 9 PM reminders)
   - Start tracking!

## 🔧 Troubleshooting

### Build Errors

**"SDK not found"**
- Install Android SDK via Android Studio
- Set ANDROID_HOME environment variable

**"Gradle sync failed"**
- Check internet connection
- File → Invalidate Caches → Restart

**"Build failed"**
- Check `app/build.gradle.kts` for errors
- Ensure all dependencies are available

### Installation Errors

**"App not installed"**
- Uninstall previous version if exists
- Check phone storage space
- Ensure APK is not corrupted

**"Parse error"**
- APK might be for wrong architecture
- Re-download/transfer APK

## 📦 APK Variants

- **Debug APK**: For testing (larger size, includes debug info)
- **Release APK**: For distribution (smaller, optimized)

For release APK:
```bash
gradlew.bat assembleRelease
```

## 🎯 Minimum Requirements

- Android 7.0 (API 24) or higher
- ~10 MB storage space
- Internet not required (offline-first)

## 🚀 After Installation

1. Complete onboarding
2. Add your first daily record
3. Check calendar to see your entry
4. Wait for 9 PM reminder (or test immediately)
5. Log entries daily to build your streak!

---

**Need help?** Open an issue on GitHub or contact the developer.
