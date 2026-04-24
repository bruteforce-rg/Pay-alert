package com.payalert.storage

import android.content.Context
import android.content.SharedPreferences
import com.payalert.data.PaymentRecord
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate

class PaymentStorage(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "payment_storage",
        Context.MODE_PRIVATE
    )

    companion object {
        private const val KEY_PAYMENTS = "payments"
    }

    fun savePayment(payment: PaymentRecord) {
        val payments = getPayments().toMutableList()
        payments.add(payment)
        savePayments(payments)
    }

    fun getPayments(): List<PaymentRecord> {
        val json = prefs.getString(KEY_PAYMENTS, "[]") ?: "[]"
        val payments = mutableListOf<PaymentRecord>()
        try {
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                payments.add(
                    PaymentRecord(
                        id = obj.optLong("id", System.currentTimeMillis()),
                        amount = obj.optDouble("amount", 0.0),
                        appName = obj.optString("appName", ""),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis()),
                        description = obj.optString("description", "")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return payments
    }

    fun getPaymentsByDate(date: LocalDate): List<PaymentRecord> {
        return getPayments().filter { it.date == date }
    }

    fun getTodayTotal(): Double {
        return getPaymentsByDate(LocalDate.now()).sumOf { it.amount }
    }

    fun getYesterdayTotal(): Double {
        return getPaymentsByDate(LocalDate.now().minusDays(1)).sumOf { it.amount }
    }

    fun getWeeklyTotal(): Double {
        val startDate = LocalDate.now().minusDays(6)
        return getPayments()
            .filter { it.date >= startDate && it.date <= LocalDate.now() }
            .sumOf { it.amount }
    }

    fun getMonthlyTotal(): Double {
        val startDate = LocalDate.now().withDayOfMonth(1)
        return getPayments()
            .filter { it.date >= startDate && it.date <= LocalDate.now() }
            .sumOf { it.amount }
    }

    fun getYearlyTotal(): Double {
        val startDate = LocalDate.now().withDayOfYear(1)
        return getPayments()
            .filter { it.date >= startDate && it.date <= LocalDate.now() }
            .sumOf { it.amount }
    }

    fun getTodayCount(): Int = getPaymentsByDate(LocalDate.now()).size
    fun getWeeklyCount(): Int {
        val startDate = LocalDate.now().minusDays(6)
        return getPayments()
            .count { it.date >= startDate && it.date <= LocalDate.now() }
    }

    fun getMonthlyCount(): Int {
        val startDate = LocalDate.now().withDayOfMonth(1)
        return getPayments()
            .count { it.date >= startDate && it.date <= LocalDate.now() }
    }

    private fun savePayments(payments: List<PaymentRecord>) {
        val array = JSONArray()
        payments.forEach { payment ->
            val obj = JSONObject().apply {
                put("id", payment.id)
                put("amount", payment.amount)
                put("appName", payment.appName)
                put("timestamp", payment.timestamp)
                put("description", payment.description)
            }
            array.put(obj)
        }
        prefs.edit().putString(KEY_PAYMENTS, array.toString()).apply()
    }
}
