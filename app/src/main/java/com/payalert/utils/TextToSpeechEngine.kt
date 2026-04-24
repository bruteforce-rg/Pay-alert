package com.payalert.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

class TextToSpeechEngine(context: Context) {
    private var tts: TextToSpeech? = null
    private var isReady = false

    init {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale("en", "IN")
                tts?.pitch = 1.5f // High pitch for alert
                tts?.setSpeechRate(0.9f)
                isReady = true
            }
        }
    }

    fun speak(text: String, interrupt: Boolean = true) {
        if (!isReady) return
        
        if (interrupt) {
            tts?.stop()
        }
        
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null)
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }
}
