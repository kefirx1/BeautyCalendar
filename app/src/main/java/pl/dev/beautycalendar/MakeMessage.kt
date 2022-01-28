package pl.dev.beautycalendar

import java.lang.StringBuilder
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

class MakeMessage {

    private val BASIC_MESSAGE1 = "Dzien dobry Salon Kosmetyczny M&A  Monika I Agata przypominaja o najblizszej wizycie "

    fun getMessage(customerInfo: CustomerInfo): String{

        val dateTimeOfVisitMillis = customerInfo.date

        val stringDate = getStringDate(dateTimeOfVisitMillis)

        return BASIC_MESSAGE1 + stringDate
    }

    private fun getStringDate(dateTimeOfVisitMillis: Long): String{
        val dateTimeOfVisit = LocalDateTime.ofInstant(Instant.ofEpochMilli(dateTimeOfVisitMillis), ZoneId.systemDefault())
        val year = dateTimeOfVisit.year
        val month = dateTimeOfVisit.monthValue
        val day = dateTimeOfVisit.dayOfMonth
        val hour = dateTimeOfVisit.hour
        val minute = dateTimeOfVisit.minute

        return "$day.$month.$year $hour:$minute"

    }

}