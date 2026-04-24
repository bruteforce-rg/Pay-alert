package com.payalert.utils

import android.content.Context
import android.media.AudioManager
import android.os.Build

class AudioManager(private val context: Context) {
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as android.media.AudioManager

    fun playAlertSound() {
        // Save current mode
        val originalMode = audioManager.ringerMode

        // Temporarily set to normal mode to play sound
        audioManager.ringerMode = android.media.AudioManager.RINGER_MODE_NORMAL

        // Set volume to max
        val maxVolume = audioManager.getStreamMaxVolume(android.media.AudioManager.STREAM_ALARM)
        audioManager.setStreamVolume(android.media.AudioManager.STREAM_ALARM, maxVolume, 0)

        // Request audio focus
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val audioFocusRequest = android.media.AudioFocusRequest.Builder(
                android.media.AudioManager.AUDIOFOCUS_GAIN
            ).build()
            audioManager.requestAudioFocus(audioFocusRequest)
        } else {
            @Suppress("DEPRECATION")
            audioManager.requestAudioFocus(
                null,
                android.media.AudioManager.STREAM_ALARM,
                android.media.AudioManager.AUDIOFOCUS_GAIN
            )
        }
    }

    fun restoreAudioSettings() {
        val maxVolume = audioManager.getStreamMaxVolume(android.media.AudioManager.STREAM_NOTIFICATION)
        val currentVolume = audioManager.getStreamVolume(android.media.AudioManager.STREAM_NOTIFICATION)
        audioManager.setStreamVolume(android.media.AudioManager.STREAM_NOTIFICATION, currentVolume, 0)
    }
}
