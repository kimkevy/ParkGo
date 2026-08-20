package com.example.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

enum class TicketStatus {
    PARKED,
    PAID,
    EXITED
}

data class ParkingTicket(
    val id: String = UUID.randomUUID().toString(),
    val regNumber: String,
    val carModel: String = "Saloon Car",
    val parkingBay: String = "Bay A-01",
    val entryTimeMillis: Long,
    val durationMinutes: Long,
    val rateKsh: Int,
    val status: TicketStatus = TicketStatus.PARKED,
    val mpesaPhone: String? = null,
    val mpesaReceiptNo: String? = null,
    val paidAtMillis: Long? = null
) {
    val formattedEntryTime: String
        get() = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(entryTimeMillis))

    val formattedPaidTime: String
        get() = paidAtMillis?.let {
            SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(it))
        } ?: "N/A"

    val formattedDuration: String
        get() {
            val hours = durationMinutes / 60
            val mins = durationMinutes % 60
            return when {
                hours > 0 && mins > 0 -> "${hours} hr ${mins} min"
                hours > 0 -> "${hours} hr${if (hours > 1) "s" else ""}"
                else -> "${mins} min${if (mins != 1L) "s" else ""}"
            }
        }
}

object ParkingCalculator {
    /**
     * Rate structure:
     * 0 - 1 hour: Ksh 50
     * 1 - 2 hours: Ksh 100
     * 2 - 3 hours: Ksh 200
     * Above 4 hours: Ksh 500 and Ksh 100 for every hour after 5 hours
     */
    fun calculateFee(durationMinutes: Long): Int {
        val totalMinutes = durationMinutes.coerceAtLeast(1)
        return when {
            totalMinutes <= 60 -> 50 // 0 - 1 hour
            totalMinutes <= 120 -> 100 // 1 - 2 hours
            totalMinutes <= 180 -> 200 // 2 - 3 hours
            totalMinutes <= 240 -> 350 // 3 - 4 hours transition tier
            totalMinutes <= 300 -> 500 // 4 - 5 hours (Above 4 hours flat base)
            else -> {
                // Above 5 hours: Ksh 500 + Ksh 100 for every additional full or partial hour
                val extraMinutes = totalMinutes - 300
                val extraHours = ((extraMinutes + 59) / 60).toInt() // ceil
                500 + (extraHours * 100)
            }
        }
    }

    fun getRateExplanation(durationMinutes: Long): String {
        val totalMinutes = durationMinutes.coerceAtLeast(1)
        return when {
            totalMinutes <= 60 -> "0–1 hr rate (Ksh 50)"
            totalMinutes <= 120 -> "1–2 hrs rate (Ksh 100)"
            totalMinutes <= 180 -> "2–3 hrs rate (Ksh 200)"
            totalMinutes <= 240 -> "3–4 hrs rate (Ksh 350)"
            totalMinutes <= 300 -> "4–5 hrs rate (Ksh 500 flat)"
            else -> {
                val extraHours = ((totalMinutes - 300 + 59) / 60).toInt()
                "5+ hrs (Ksh 500 + $extraHours hr @ Ksh 100/hr = Ksh ${500 + extraHours * 100})"
            }
        }
    }
}
