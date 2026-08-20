package com.example.data

import com.example.model.ParkingCalculator
import com.example.model.ParkingTicket
import com.example.model.TicketStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Locale
import kotlin.random.Random

class ParkingRepository {

    private val _tickets = MutableStateFlow<List<ParkingTicket>>(emptyList())
    val tickets: StateFlow<List<ParkingTicket>> = _tickets.asStateFlow()

    init {
        seedInitialMockData()
    }

    private fun seedInitialMockData() {
        val now = System.currentTimeMillis()

        // Samples requested in prompt:
        // KDC 501T - 30 minutes
        // KDG 152S - 6 hours
        // KCG 741G - 3 hours 10 minutes
        // KBV 235H - 2 hours
        // KCC 114K - 4 hours
        // KAD 102U - 1 hour 25 minutes

        val sampleConfigs = listOf(
            SampleCar("KDC 501T", "Toyota Premio", "Bay A-03", 30L),
            SampleCar("KDG 152S", "Mazda CX-5", "Bay C-12", 360L),
            SampleCar("KCG 741G", "Subaru Outback", "Bay B-07", 190L),
            SampleCar("KBV 235H", "Honda Fit", "Bay A-11", 120L),
            SampleCar("KCC 114K", "Mercedes C200", "Bay VIP-01", 240L),
            SampleCar("KAD 102U", "Nissan X-Trail", "Bay B-04", 85L)
        )

        val initialList = sampleConfigs.map { sample ->
            val entryTime = now - (sample.durationMins * 60 * 1000)
            val fee = ParkingCalculator.calculateFee(sample.durationMins)
            ParkingTicket(
                regNumber = sample.reg,
                carModel = sample.model,
                parkingBay = sample.bay,
                entryTimeMillis = entryTime,
                durationMinutes = sample.durationMins,
                rateKsh = fee,
                status = TicketStatus.PARKED
            )
        }

        _tickets.value = initialList
    }

    fun findTicketByReg(query: String): ParkingTicket? {
        val clean = query.replace(" ", "").uppercase(Locale.getDefault())
        if (clean.isBlank()) return null
        return _tickets.value.firstOrNull {
            it.regNumber.replace(" ", "").uppercase(Locale.getDefault()) == clean
        }
    }

    fun checkInNewCar(regNumber: String, carModel: String, durationMinutes: Long = 30L): ParkingTicket {
        val formattedReg = formatKenyanReg(regNumber)
        val bayLetter = listOf("A", "B", "C", "D", "E").random()
        val bayNum = Random.nextInt(1, 40)
        val bay = "Bay $bayLetter-${bayNum.toString().padStart(2, '0')}"
        val now = System.currentTimeMillis()
        val entryTime = now - (durationMinutes * 60 * 1000)
        val fee = ParkingCalculator.calculateFee(durationMinutes)

        val newTicket = ParkingTicket(
            regNumber = formattedReg,
            carModel = carModel.ifBlank { "Vehicle" },
            parkingBay = bay,
            entryTimeMillis = entryTime,
            durationMinutes = durationMinutes,
            rateKsh = fee,
            status = TicketStatus.PARKED
        )

        _tickets.update { list ->
            // Replace if already exists with same reg, otherwise prepend
            val filtered = list.filterNot { it.regNumber.replace(" ", "").equals(formattedReg.replace(" ", ""), ignoreCase = true) }
            listOf(newTicket) + filtered
        }

        return newTicket
    }

    fun updateDuration(ticketId: String, newDurationMinutes: Long) {
        _tickets.update { list ->
            list.map { ticket ->
                if (ticket.id == ticketId && ticket.status == TicketStatus.PARKED) {
                    val newFee = ParkingCalculator.calculateFee(newDurationMinutes)
                    val newEntryTime = System.currentTimeMillis() - (newDurationMinutes * 60 * 1000)
                    ticket.copy(
                        durationMinutes = newDurationMinutes,
                        rateKsh = newFee,
                        entryTimeMillis = newEntryTime
                    )
                } else {
                    ticket
                }
            }
        }
    }

    fun completeMpesaPayment(
        ticketId: String,
        phoneNumber: String,
        mpesaReceiptCode: String
    ): ParkingTicket? {
        var updatedTicket: ParkingTicket? = null
        _tickets.update { list ->
            list.map { ticket ->
                if (ticket.id == ticketId) {
                    val updated = ticket.copy(
                        status = TicketStatus.PAID,
                        mpesaPhone = phoneNumber,
                        mpesaReceiptNo = mpesaReceiptCode,
                        paidAtMillis = System.currentTimeMillis()
                    )
                    updatedTicket = updated
                    updated
                } else {
                    ticket
                }
            }
        }
        return updatedTicket
    }

    fun markTicketExited(ticketId: String) {
        _tickets.update { list ->
            list.map { ticket ->
                if (ticket.id == ticketId) {
                    ticket.copy(status = TicketStatus.EXITED)
                } else {
                    ticket
                }
            }
        }
    }

    fun resetMockData() {
        seedInitialMockData()
    }

    companion object {
        fun generateMpesaReceiptCode(): String {
            val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
            val prefix = "R" + (1..2).map { chars.random() }.joinToString("")
            val middle = (1..4).map { (0..9).random() }.joinToString("")
            val suffix = (1..3).map { chars.random() }.joinToString("")
            return "$prefix$middle$suffix"
        }

        fun formatKenyanReg(raw: String): String {
            val clean = raw.trim().uppercase(Locale.getDefault()).replace("\\s+".toRegex(), "")
            if (clean.length >= 6 && clean.startsWith("K")) {
                // e.g. KDC501T -> KDC 501T
                val prefix = clean.substring(0, 3)
                val rest = clean.substring(3)
                return "$prefix $rest"
            }
            return clean.ifBlank { "KAA 001A" }
        }
    }

    private data class SampleCar(
        val reg: String,
        val model: String,
        val bay: String,
        val durationMins: Long
    )
}
