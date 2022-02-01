package pl.dev.beautycalendar.classes

import android.app.Activity
import pl.dev.beautycalendar.MainActivity
import pl.dev.beautycalendar.data.CustomerInfo
import pl.dev.beautycalendar.data.VisitsDate

object Customer {

    fun getCustomer(customerId: String, service: String, dateOfVisit: Long): CustomerInfo {

        val emptyList: ArrayList<VisitsDate> = ArrayList()
        var customerNewVisit = CustomerInfo(1, emptyList, "", "", "")

        MainActivity.customersList.forEach {
            if (it.telephone == customerId) {
                customerNewVisit = it
            }
        }

        val newVisitDate = VisitsDate(dateOfVisit, service)

        customerNewVisit.active = 1
        customerNewVisit.dateOf.add(newVisitDate)

        return customerNewVisit
    }

    fun getCustomer(customerId: String): CustomerInfo {

        val emptyList: ArrayList<VisitsDate> = ArrayList()
        var customerNewVisit = CustomerInfo(1, emptyList, "", "", "")

        MainActivity.customersList.forEach {
            if (it.telephone == customerId) {
                customerNewVisit = it
            }
        }

        return customerNewVisit
    }


    fun getCustomerId(customerName: String): String {

        val indexOfFirstSpace = customerName.indexOf(" ")

        return customerName.substring(0, indexOfFirstSpace)
    }



}