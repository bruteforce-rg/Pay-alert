# Pay Alert - Payment Notification Alert App

## Overview
Pay Alert is an Android application that monitors incoming payment notifications from popular payment apps like PhonePe, Google Pay, Paytm, etc. and converts them to voice alerts with high-tone sound playback, even when the device screen is locked.

## Features

### 🔔 Smart Notification Detection
- Automatically detects payment notifications from:
  - PhonePe
  - Google Pay
  - Paytm
  - Amazon Pay
  - WhatsApp Pay
  - Bank Apps (SBI, HDFC, ICICI, Axis)
  - And more...

### 🔊 High-Tone Audio Alerts
- Plays loud alerts even when device is in silent mode
- Voice-based payment announcements with TTS
- Customizable alert frequency and tone

### 📊 Comprehensive Dashboard
- Real-time payment statistics
- Daily summary of payments received
- Weekly, monthly, and yearly analytics
- Transaction count tracking
- Beautiful UI with stat cards

### 💾 Local Data Storage
- All payment data stored locally on device
- No cloud synchronization or backend required
- Full privacy - your data stays on your phone
- Automatic history tracking

## Technical Stack

### Architecture
- **Language**: Kotlin (100% Kotlin codebase)
- **Framework**: Android Jetpack & Material Components
- **Min SDK**: Android 8.0 (API 26)
- **Target SDK**: Android 14 (API 34)

### Key Components

#### 1. **PaymentNotificationService** (`services/PaymentNotificationService.kt`)
- System-level NotificationListenerService
- Intercepts incoming notifications in real-time
- Parses payment information from notification text
- Triggers audio and voice alerts

#### 2. **Payment Parser** (`utils/PaymentParser.kt`)
- Regex-based amount extraction from notification text
- Supports multiple formats (₹, Rs, rupees, etc.)
- Handles formatted amounts (1,000, 10,50, etc.)
- Generates natural speech text

#### 3. **TextToSpeechEngine** (`utils/TextToSpeechEngine.kt`)
- High-pitch voice for alert notifications
- Natural speech with Hindi locale support
- Interrupt capability for urgent alerts
- Clean shutdown handling

#### 4. **Audio Manager** (`utils/AudioManagerHelper.kt`)
- Override silent/vibrate modes
- Set maximum volume levels
- Audio focus management
- Works on locked screen

#### 5. **Payment Storage** (`storage/PaymentStorage.kt`)
- JSON-based local storage using SharedPreferences
- Date-wise payment tracking
- Statistical queries (daily, weekly, monthly, yearly)
- Transaction counting

#### 6. **Main Dashboard** (`ui/MainActivity.kt`)
- Beautiful stat cards for each time period
- One-click notification access setup
- Real-time statistics updates
- Material Design UI

## Installation

### Prerequisites
- Android Studio 2023.1 or higher
- Android SDK 34
- Gradle 8.1.0
- Kotlin 1.9.0

### Build Steps

1. **Clone the repository**
```bash
cd "c:\Users\Rakesh\Desktop\Pay Alert"
```

2. **Open in Android Studio**
- File → Open → Select the Pay Alert directory

3. **Sync Gradle**
- Android Studio will prompt to sync - click "Sync Now"

4. **Build the APK**
- Build → Build Bundle(s) / APK(s) → Build APK

5. **Run on Device/Emulator**
- Run → Run 'app' (or Shift + F10)

## Usage

### First Time Setup

1. **Install the App**
   - Build and install on Android device (Android 8.0+)

2. **Enable Notification Access**
   - Open the app
   - Click "Enable Notification Listener"
   - Go to Settings → Notifications → Notification Access
   - Find "Pay Alert" and toggle ON

3. **Grant Required Permissions**
   - Notification reading permission (automatic)
   - System alert window permission (for overlay alerts)

### Daily Usage

1. Receive a payment notification from any payment app
2. App automatically:
   - Detects the notification
   - Parses the amount
   - Speaks "Received [amount] rupees in [app name]"
   - Plays high-tone alert sound
3. View statistics on dashboard:
   - Today's total and count
   - Yesterday's total
   - Weekly total and count
   - Monthly total and count
   - Yearly total

## Data Storage Format

### Payment Records (JSON)
```json
{
  "id": 1234567890,
  "amount": 500.00,
  "appName": "PhonePe",
  "timestamp": 1234567890000,
  "description": "Payment received notification text"
}
```

### Storage Location
- **Method**: SharedPreferences
- **File**: `/data/data/com.payalert/shared_prefs/payment_storage.xml`
- **Key**: "payments"

## Supported Payment Apps

| App | Package Name | Status |
|-----|--------------|--------|
| PhonePe | com.phonepe.app | ✅ Supported |
| Google Pay | com.google.android.apps.nbu.paisa.user | ✅ Supported |
| Paytm | com.paytm | ✅ Supported |
| Amazon Pay | com.amazon.venezia | ✅ Supported |
| WhatsApp Pay | com.whatsapp | ✅ Supported |
| SBI YONO | com.sbi.lotusintouch | ✅ Supported |
| HDFC Bank | com.rediffmoney.etrade | ✅ Supported |
| ICICI Bank | com.infinity | ✅ Supported |
| Axis Bank | com.axis.pdf | ✅ Supported |

## Permissions Required

```xml
<uses-permission android:name="android.permission.BIND_NOTIFICATION_LISTENER_SERVICE" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
```

## File Structure

```
Pay Alert/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── AndroidManifest.xml
│   │   │   ├── java/com/payalert/
│   │   │   │   ├── PayAlertApp.kt
│   │   │   │   ├── data/
│   │   │   │   │   └── PaymentRecord.kt
│   │   │   │   ├── services/
│   │   │   │   │   └── PaymentNotificationService.kt
│   │   │   │   ├── storage/
│   │   │   │   │   └── PaymentStorage.kt
│   │   │   │   ├── ui/
│   │   │   │   │   ├── MainActivity.kt
│   │   │   │   │   └── PaymentHistoryFragment.kt
│   │   │   │   └── utils/
│   │   │   │       ├── AudioManagerHelper.kt
│   │   │   │       ├── PaymentParser.kt
│   │   │   │       └── TextToSpeechEngine.kt
│   │   │   └── res/
│   │   │       ├── drawable/
│   │   │       │   └── ic_launcher.xml
│   │   │       ├── layout/
│   │   │       │   ├── activity_main.xml
│   │   │       │   ├── payment_item.xml
│   │   │       │   └── stat_card.xml
│   │   │       └── values/
│   │   │           ├── colors.xml
│   │   │           ├── strings.xml
│   │   │           └── styles.xml
│   ├── build.gradle
│   └── proguard-rules.pro
├── build.gradle
└── settings.gradle
```

## Future Enhancements

- [ ] Ignore list for selective payment apps
- [ ] Custom alert sounds
- [ ] Notification categorization by merchant
- [ ] Export statistics to CSV
- [ ] Dark mode support
- [ ] Multiple language support
- [ ] Scheduled quiet hours
- [ ] Custom TTS voice configuration
- [ ] Backup and restore functionality
- [ ] Widget for quick stats on home screen

## Troubleshooting

### App Not Detecting Payments?
1. Ensure notification access is enabled in Settings
2. Check if payment app's notification is not disabled
3. Try reopening the app with fresh notification

### Volume Not Loud Enough?
1. Go to device Settings → Volume
2. Set Media or Notifications volume to maximum
3. Disable Do Not Disturb or Silent mode

### App Crashing on Startup?
1. Clear app data: Settings → Apps → Pay Alert → Storage → Clear Data
2. Reinstall the app
3. Report issue with logcat output

## Development Notes

### Adding New Payment Apps
Edit `PaymentParser.kt`:
```kotlin
private val paymentApps = mapOf(
    "newapp" to "New App Name"
    // Add more apps here
)
```

### Customizing Alerts
Edit `PaymentNotificationService.kt`:
- Modify `ttsEngine.speak()` for custom speech text
- Adjust `audioManager.playAlertSound()` for volume levels

### Testing
1. Use Android Studio's notification simulation
2. Or receive actual test payments from payment apps
3. Monitor logcat for debugging

## License
This project is open source and available for personal and commercial use.

## Support
For issues, questions, or feature requests, please contact the developer.

---

**Version**: 1.0
**Last Updated**: April 24, 2026
**Author**: Your Name
