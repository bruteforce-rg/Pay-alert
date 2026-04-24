package com.payalert.ui

import android.content.Intent
import android.provider.Settings
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Button
import android.widget.ScrollView
import android.view.ViewGroup
import android.graphics.Color
import android.graphics.Typeface
import com.payalert.storage.PaymentStorage
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {
    private lateinit var storage: PaymentStorage
    private val decimalFormat = DecimalFormat("0.00")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        storage = PaymentStorage(this)
        checkNotificationListenerPermission()
        buildUI()
    }

    private fun buildUI() {
        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        // Scrollable content
        val scrollView = ScrollView(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0
            ).apply { (this as? ViewGroup.LayoutParams)?.let { 
                if (this is LinearLayout.LayoutParams) weight = 1f 
            } }
        }

        val contentLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setPadding(20, 30, 20, 20)

            // Title
            addView(TextView(this@MainActivity).apply {
                text = "Payment Alerts Dashboard"
                textSize = 26f
                setTypeface(null, Typeface.BOLD)
                setTextColor(Color.BLACK)
                setPadding(0, 0, 0, 25)
            })

            // Statistics Cards
            addView(createStatCard(
                "Today",
                "₹${decimalFormat.format(storage.getTodayTotal())}",
                "${storage.getTodayCount()} transactions"
            ))

            addView(createStatCard(
                "Yesterday",
                "₹${decimalFormat.format(storage.getYesterdayTotal())}",
                "Last day"
            ))

            addView(createStatCard(
                "This Week",
                "₹${decimalFormat.format(storage.getWeeklyTotal())}",
                "${storage.getWeeklyCount()} transactions"
            ))

            addView(createStatCard(
                "This Month",
                "₹${decimalFormat.format(storage.getMonthlyTotal())}",
                "${storage.getMonthlyCount()} transactions"
            ))

            addView(createStatCard(
                "This Year",
                "₹${decimalFormat.format(storage.getYearlyTotal())}",
                "Year to date"
            ))
        }

        scrollView.addView(contentLayout)
        val scrollParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            0
        ).apply { weight = 1f }
        mainLayout.addView(scrollView, scrollParams)

        // Bottom button
        val buttonLayout = LinearLayout(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setPadding(20, 0, 20, 20)
        }

        buttonLayout.addView(Button(this).apply {
            text = "Enable Notification Listener"
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setOnClickListener {
                openNotificationSettings()
            }
        })

        mainLayout.addView(buttonLayout)
        setContentView(mainLayout)
    }

    private fun createStatCard(title: String, amount: String, subtitle: String): LinearLayout {
        val marginParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(0, 0, 0, 15)
        }

        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = marginParams
            setPadding(20, 20, 20, 20)
            setBackgroundColor(Color.parseColor("#F5F5F5"))

            // Title
            addView(TextView(this@MainActivity).apply {
                text = title
                textSize = 14f
                setTextColor(Color.GRAY)
            })

            // Amount
            addView(TextView(this@MainActivity).apply {
                text = amount
                textSize = 28f
                setTypeface(null, Typeface.BOLD)
                setTextColor(Color.BLACK)
                setPadding(0, 10, 0, 0)
            })

            // Subtitle
            addView(TextView(this@MainActivity).apply {
                text = subtitle
                textSize = 12f
                setTextColor(Color.DKGRAY)
                setPadding(0, 5, 0, 0)
            })
        }
    }

    private fun checkNotificationListenerPermission() {
        val enabledListeners = Settings.Secure.getString(
            contentResolver,
            "enabled_notification_listeners"
        ) ?: ""

        val isEnabled = enabledListeners.contains(packageName)

        if (!isEnabled) {
            Toast.makeText(
                this,
                "Please enable notification access for this app",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun openNotificationSettings() {
        startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
    }

    override fun onResume() {
        super.onResume()
        // Refresh UI to update statistics
        buildUI()
    }
}
