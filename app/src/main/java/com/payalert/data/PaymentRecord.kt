package com.payalert.data

import java.time.LocalDate

data class PaymentRecord(
    val id: Long = System.currentTimeMillis(),
    val amount: Double = 0.0,
    val appName: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val description: String = ""
) {
    val date: LocalDate
        get() = java.time.Instant.ofEpochMilli(timestamp)
            .atZone(java.time.ZoneId.systemDefault())
            .toLocalDate()
}

data class DailyStats(
    val date: LocalDate,
    val totalAmount: Double,
    val transactionCount: Int
)
