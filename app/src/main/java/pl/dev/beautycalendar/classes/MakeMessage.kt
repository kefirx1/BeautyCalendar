package pl.dev.beautycalendar.classes

import pl.dev.beautycalendar.data.CustomerInfo
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class MakeMessage {

    private val basicMessage = "Dzien dobry Salon Kosmetyczny M&A  Monika I Agata przypominaja o najblizszej wizycie "

    fun getMessage(customerInfo: CustomerInfo): String{

        val dateTimeOfVisitMillis = customerInfo.dateOf[customerInfo.dateOf.size-1].date

        val stringDate = getStringDate(dateTimeOfVisitMillis)

        return basicMessage + stringDate
    }

    private fun getStringDate(dateTimeOfVisitMillis: Long): String{
        val dateTimeOfVisit = LocalDateTime.ofInstant(Instant.ofEpochMilli(dateTimeOfVisitMillis), ZoneId.systemDefault())
        val year = dateTimeOfVisit.year
        val month = dateTimeOfVisit.monthValue
        val day = dateTimeOfVisit.dayOfMonth
        val hour = dateTimeOfVisit.hour
        var minute = dateTimeOfVisit.minute.toString()

        if(dateTimeOfVisit.minute<10){
            minute = "0$minute"
        }

        return "$day.$month.$year, godzina $hour:$minute"

    }

}