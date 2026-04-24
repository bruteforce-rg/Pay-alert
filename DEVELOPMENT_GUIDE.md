# Development Guide - Pay Alert App

## Architecture Overview

The Pay Alert app uses a layered architecture with clear separation of concerns:

```
┌─────────────────────────────────────────┐
│       User Interface Layer              │
│  (MainActivity.kt, Fragments)           │
└──────────────┬──────────────────────────┘
               │
┌──────────────▼──────────────────────────┐
│       Business Logic Layer              │
│  (PaymentParser, AudioManager)          │
└──────────────┬──────────────────────────┘
               │
┌──────────────▼──────────────────────────┐
│       Service Layer                     │
│  (PaymentNotificationService, TTS)      │
└──────────────┬──────────────────────────┘
               │
┌──────────────▼──────────────────────────┐
│       Data Layer                        │
│  (PaymentStorage, SharedPreferences)    │
└─────────────────────────────────────────┘
```

## Key Components

### 1. PaymentNotificationService
**Location:** `services/PaymentNotificationService.kt`

Runs as a system service that intercepts all notifications.

**Key Methods:**
- `onNotificationPosted()` - Called when new notification arrives
- `parseNotification()` - Extract payment info
- `triggerAlert()` - Play sound and voice

**Flow:**
```
Notification Posted
    ↓
Check if it's payment app
    ↓ (Yes)
Parse amount & app name
    ↓
Save to storage
    ↓
Play audio alert
    ↓
Speak text-to-speech
```

### 2. PaymentStorage
**Location:** `storage/PaymentStorage.kt`

Manages all data persistence without database.

**Key Methods:**
- `savePayment()` - Add new payment
- `getTodayTotal()` - Sum of today's payments
- `getWeeklyTotal()` - Sum of week's payments
- `getPaymentsByDate()` - Filter by date

**Data Format:**
Uses JSON array stored in SharedPreferences:
```json
[
  {
    "id": 1234567890,
    "amount": 500.0,
    "appName": "PhonePe",
    "timestamp": 1234567890000,
    "description": "Payment received..."
  }
]
```

### 3. TextToSpeechEngine
**Location:** `utils/TextToSpeechEngine.kt`

Handles speech synthesis with customization.

**Key Methods:**
- `speak()` - Speak text with TTS
- `shutdown()` - Clean up resources

**Features:**
- High pitch (1.5x) for alert
- Slow speech rate (0.9x) for clarity
- Hindi locale (en_IN) for Indian context

### 4. PaymentParser
**Location:** `utils/PaymentParser.kt`

Extracts payment information from notification text.

**Key Methods:**
- `parseAmount()` - Extract rupee amount using regex
- `getAppName()` - Identify payment app
- `generateSpeechText()` - Create natural speech

**Regex Patterns Supported:**
- `₹500` - Rupee symbol
- `Rs 500` - Short form
- `500 rupees` - Word form
- `received 500` - Past tense
- `1,000` - Comma separated

### 5. MainActivity
**Location:** `ui/MainActivity.kt`

Displays statistics dashboard and controls.

**Key Features:**
- Statistics cards (Today, Yesterday, Week, Month, Year)
- Settings button for notification access
- Real-time stat updates on resume
- Programmatic UI (no XML layout inflation)

## Data Flow

### Payment Detection Flow
```
1. User receives PhonePe payment
   ↓
2. Phone receives notification
   ↓
3. PaymentNotificationService.onNotificationPosted() called
   ↓
4. Extract: title, text, largeText from notification
   ↓
5. isPaymentApp(packageName) check
   ↓ (True)
6. parseAmount(fullText) with regex
   ↓ (Found: ₹500)
7. getAppName(packageName) → "PhonePe"
   ↓
8. Create PaymentRecord(amount=500, app="PhonePe")
   ↓
9. storage.savePayment(payment)
   ↓
10. audioManager.playAlertSound() (max volume)
   ↓
11. ttsEngine.speak("Received 500 rupees in PhonePe")
   ↓
12. MainActivity updates stats card (on next onResume)
```

## File Structure

```
Pay Alert/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── AndroidManifest.xml
│   │       │   └── Declares NotificationListenerService
│   │       │   └── Declares permissions
│   │       │
│   │       ├── java/com/payalert/
│   │       │   ├── PayAlertApp.kt (App class)
│   │       │   │
│   │       │   ├── data/
│   │       │   │   └── PaymentRecord.kt (Data model)
│   │       │   │
│   │       │   ├── services/
│   │       │   │   └── PaymentNotificationService.kt (Main service)
│   │       │   │
│   │       │   ├── storage/
│   │       │   │   └── PaymentStorage.kt (Data persistence)
│   │       │   │
│   │       │   ├── ui/
│   │       │   │   ├── MainActivity.kt (Dashboard UI)
│   │       │   │   └── PaymentHistoryFragment.kt (History view)
│   │       │   │
│   │       │   └── utils/
│   │       │       ├── AudioManagerHelper.kt
│   │       │       ├── PaymentParser.kt
│   │       │       └── TextToSpeechEngine.kt
│   │       │
│   │       └── res/
│   │           ├── drawable/
│   │           │   ├── ic_launcher.xml
│   │           │   └── ic_launcher_background.xml
│   │           │
│   │           ├── layout/
│   │           │   ├── activity_main.xml
│   │           │   ├── payment_item.xml
│   │           │   └── stat_card.xml
│   │           │
│   │           └── values/
│   │               ├── colors.xml
│   │               ├── strings.xml
│   │               └── styles.xml
│   │
│   ├── build.gradle (App-level build config)
│   └── proguard-rules.pro (Code obfuscation)
│
├── build.gradle (Project-level config)
├── settings.gradle (Gradle settings)
├── gradle.properties (Gradle properties)
├── gradlew & gradlew.bat (Gradle wrapper scripts)
├── .gitignore (Git ignore patterns)
├── README.md (Full documentation)
└── QUICK_START.md (Quick setup guide)
```

## Adding New Payment Apps

### Step 1: Update PaymentParser.kt
```kotlin
private val paymentApps = mapOf(
    "phonepe" to "PhonePe",
    "googlepay" to "Google Pay",
    "mynewapp" to "My New App"  // Add this line
)
```

### Step 2: Test
- Receive notification from new app
- Verify it's detected and parsed

## Customization Ideas

### 1. Custom Alert Sounds
Replace in `PaymentNotificationService.kt`:
```kotlin
audioManager.playAlertSound()
// Replace with custom sound playback:
// val uri = Uri.parse("android.resource://${packageName}/raw/alert")
// MediaPlayer.create(this, uri).start()
```

### 2. Add Merchant Detection
In `PaymentParser.kt`, enhance to extract merchant name:
```kotlin
fun parseMerchant(text: String): String? {
    // Extract merchant name using regex
}
```

### 3. Quiet Hours
Add in `PaymentNotificationService.kt`:
```kotlin
private fun isQuietHours(): Boolean {
    val hour = LocalDateTime.now().hour
    return hour < 7 || hour > 22  // 10 PM to 7 AM
}
```

### 4. Export Statistics
In `PaymentStorage.kt`:
```kotlin
fun exportToCSV(file: File) {
    // Write payments to CSV format
}
```

## Testing Strategy

### Unit Tests
Create `PaymentParserTest.kt`:
```kotlin
@Test
fun testParseAmount() {
    val amount = PaymentParser.parseAmount("₹500 received")
    assertEquals(500.0, amount)
}
```

### Integration Tests
Test notification interception with mock notifications.

### Manual Testing
1. Test with real payment apps
2. Test with locked screen
3. Test with silent/vibrate modes
4. Test statistics accuracy

## Performance Considerations

- **Notification Parsing**: Regex operations are lightweight
- **Storage**: JSON serialization is fast for small datasets (< 1000 records)
- **TTS**: Lazy initialized on first use
- **Audio Manager**: Minimal overhead, system API

## Security Notes

- All data stored locally - no server communication
- No personal data except payment amounts
- No tracking or analytics
- No internet permissions required

## Building for Release

### Step 1: Create Signing Key
```bash
keytool -genkey -v -keystore my-release-key.keystore 
  -keyalg RSA -keysize 2048 -validity 10000 
  -alias my-key-alias
```

### Step 2: Configure Signing
Edit `build.gradle`:
```gradle
signingConfigs {
    release {
        storeFile file("my-release-key.keystore")
        storePassword "yourpassword"
        keyAlias "my-key-alias"
        keyPassword "yourpassword"
    }
}
```

### Step 3: Build Release APK
```bash
./gradlew assembleRelease
```

## Troubleshooting Build Issues

### Gradle Sync Fails
```bash
./gradlew clean
./gradlew sync
```

### Kotlin Compilation Error
- Check Kotlin version in build.gradle
- Ensure Java 8 compatibility

### Runtime Exceptions
- Check AndroidStudio Logcat for stack trace
- Enable debugging in build.gradle

---

**Happy Developing!** 🚀
