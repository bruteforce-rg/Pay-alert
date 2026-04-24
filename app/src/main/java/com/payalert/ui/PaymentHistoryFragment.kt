package com.payalert.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.payalert.storage.PaymentStorage
import java.text.SimpleDateFormat
import java.util.Locale

class PaymentHistoryFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var storage: PaymentStorage

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return RecyclerView(requireContext()).apply {
            recyclerView = this
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        storage = PaymentStorage(requireContext())
        loadPayments()
    }

    private fun loadPayments() {
        val payments = storage.getPayments().sortedByDescending { it.timestamp }
        val adapter = PaymentAdapter(payments)
        recyclerView.adapter = adapter
    }

    private inner class PaymentAdapter(private val payments: List<com.payalert.data.PaymentRecord>) :
        RecyclerView.Adapter<PaymentViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentViewHolder {
            val view = android.widget.LinearLayout(parent.context).apply {
                orientation = android.widget.LinearLayout.VERTICAL
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                setPadding(20, 20, 20, 20)
            }
            return PaymentViewHolder(view)
        }

        override fun onBindViewHolder(holder: PaymentViewHolder, position: Int) {
            holder.bind(payments[position])
        }

        override fun getItemCount() = payments.size
    }

    private inner class PaymentViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        fun bind(payment: com.payalert.data.PaymentRecord) {
            (itemView as android.widget.LinearLayout).apply {
                removeAllViews()

                // Amount
                addView(android.widget.TextView(context).apply {
                    text = "₹${payment.amount}"
                    textSize = 18f
                    setTypeface(typeface, android.graphics.Typeface.BOLD)
                })

                // App name
                addView(android.widget.TextView(context).apply {
                    text = payment.appName
                    textSize = 14f
                    setTextColor(android.graphics.Color.GRAY)
                })

                // Time
                val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                addView(android.widget.TextView(context).apply {
                    text = dateFormat.format(payment.timestamp)
                    textSize = 12f
                    setTextColor(android.graphics.Color.DKGRAY)
                })
            }
        }
    }
}
