package com.payalert.services

import android.content.Context
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.core.app.NotificationCompat
import com.payalert.data.PaymentRecord
import com.payalert.storage.PaymentStorage
import com.payalert.utils.AudioManager
import com.payalert.utils.PaymentAppDetector
import com.payalert.utils.PaymentParser
import com.payalert.utils.TextToSpeechEngine

class PaymentNotificationService : NotificationListenerService() {

    private lateinit var ttsEngine: TextToSpeechEngine
    private lateinit var storage: PaymentStorage
    private lateinit var audioManager: AudioManager

    override fun onCreate() {
        super.onCreate()
        ttsEngine = TextToSpeechEngine(this)
        storage = PaymentStorage(this)
        audioManager = AudioManager(this)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)

        val notification = sbn.notification
        val extras = notification.extras

        val packageName = sbn.packageName
        val title = extras.getCharSequence(NotificationCompat.EXTRA_TITLE)?.toString() ?: ""
        val text = extras.getCharSequence(NotificationCompat.EXTRA_TEXT)?.toString() ?: ""
        val largeText = extras.getCharSequence(NotificationCompat.EXTRA_BIG_TEXT)?.toString() ?: ""

        val fullText = "$title $text $largeText"

        // Check if it's a payment app
        if (!PaymentAppDetector.isPaymentApp(packageName)) {
            return
        }

        // Try to parse the amount from notification
        val amount = PaymentParser.parseAmount(fullText) ?: return

        // Get app name
        val appName = PaymentAppDetector.getAppName(packageName)

        // Create payment record
        val payment = PaymentRecord(
            amount = amount,
            appName = appName,
            description = fullText.take(100),
            timestamp = System.currentTimeMillis()
        )

        // Save payment
        storage.savePayment(payment)

        // Generate speech
        val speechText = PaymentParser.generateSpeechText(amount, appName)

        // Play alert
        audioManager.playAlertSound()

        // Speak the payment alert
        ttsEngine.speak(speechText)
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        super.onNotificationRemoved(sbn)
    }

    override fun onDestroy() {
        super.onDestroy()
        ttsEngine.shutdown()
    }
}
