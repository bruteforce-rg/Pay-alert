package com.payalert.utils

object PaymentAppDetector {
    private val paymentApps = mapOf(
        "phonepe" to "PhonePe",
        "googlepay" to "Google Pay",
        "paytm" to "Paytm",
        "amazonpay" to "Amazon Pay",
        "whatsapp" to "WhatsApp",
        "bankapp" to "Bank App",
        "sbi" to "SBI",
        "hdfc" to "HDFC",
        "icici" to "ICICI",
        "axis" to "Axis Bank"
    )

    fun getAppName(packageName: String?): String {
        if (packageName == null) return "Unknown App"
        return paymentApps.entries
            .find { packageName.lowercase().contains(it.key) }?.value ?: "Payment App"
    }

    fun isPaymentApp(packageName: String?): Boolean {
        if (packageName == null) return false
        val pkg = packageName.lowercase()
        return paymentApps.keys.any { pkg.contains(it) }
    }
}

object PaymentParser {
    fun parseAmount(text: String): Double? {
        // Pattern to match amount like "₹500", "500 rupees", "Rs 500", etc.
        val patterns = listOf(
            "₹[\\s]*([0-9,]+(?:\\.[0-9]{2})?)",
            "Rs[.\\s]*([0-9,]+(?:\\.[0-9]{2})?)",
            "rupees?[\\s]*([0-9,]+(?:\\.[0-9]{2})?)",
            "received[\\s]*([0-9,]+(?:\\.[0-9]{2})?)",
            "amount[\\s]*([0-9,]+(?:\\.[0-9]{2})?)",
            "₹?\\s*([0-9,]+(?:\\.[0-9]{2})?)"
        )

        for (pattern in patterns) {
            try {
                val regex = Regex(pattern, RegexOption.IGNORE_CASE)
                val match = regex.find(text)
                if (match != null) {
                    val amountStr = match.groupValues[1].replace(",", "")
                    return amountStr.toDoubleOrNull()
                }
            } catch (e: Exception) {
                continue
            }
        }
        return null
    }

    fun generateSpeechText(amount: Double, appName: String): String {
        return "Received $amount rupees in $appName"
    }
}
