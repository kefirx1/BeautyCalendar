package pl.dev.beautycalendar.classes

import pl.dev.beautycalendar.data.CustomerInfo

class MakeMessage {

    private val basicMessage =
        "Dzien dobry Salon Kosmetyczny M&A  Monika I Agata przypominaja o najblizszej wizycie "

    fun getMessageString(customerInfo: CustomerInfo): String {

        val dateTimeOfVisitMillis = customerInfo.dateOf[customerInfo.dateOf.size - 1].date

        val stringDate = DateTimeConverter.getStringDateTime(dateTimeOfVisitMillis)

        return basicMessage + stringDate
    }

    fun getMessageId(customerInfo: CustomerInfo) =
        (customerInfo.dateOf[customerInfo.dateOf.size - 1].date / 1000 / 60).toInt() + customerInfo.telephone.toInt()


}