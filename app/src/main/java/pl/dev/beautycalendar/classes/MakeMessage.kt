package pl.dev.beautycalendar.classes

import pl.dev.beautycalendar.data.CustomerInfo

class MakeMessage {

    private val basicMessage = "Dzien dobry Salon Kosmetyczny M&A  Monika I Agata przypominaja o najblizszej wizycie "
    private val dateOfVisits = DateOfVisits()

    fun getMessage(customerInfo: CustomerInfo): String{

        val dateTimeOfVisitMillis = customerInfo.dateOf[customerInfo.dateOf.size-1].date

        val stringDate = dateOfVisits.getStringDateTime(dateTimeOfVisitMillis)

        return basicMessage + stringDate
    }

}