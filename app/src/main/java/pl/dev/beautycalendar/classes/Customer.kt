package pl.dev.beautycalendar.classes

import android.app.Activity
import android.content.Intent
import android.net.Uri
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

    fun getCustomersNameList(): ArrayList<String> {

        val customersNameList: ArrayList<String> = ArrayList()

        MainActivity.customersList.forEach {
            customersNameList.add("${it.telephone} ${it.name} ${it.surname}")
        }

        return customersNameList
    }

    fun messageToCustomer(customer: CustomerInfo, instance: Activity) {
        val messageIntent = Intent(Intent.ACTION_VIEW)
        messageIntent.type = "vnd.android-dir/mms-sms"
        messageIntent.putExtra("address", customer.telephone)
        instance.startActivity(messageIntent)
    }

    fun callToCustomer(customer: CustomerInfo, instance: Activity) {
        val dialIntent = Intent(Intent.ACTION_DIAL)
        dialIntent.data = Uri.parse("tel:" + customer.telephone)
        instance.startActivity(dialIntent)
    }


}