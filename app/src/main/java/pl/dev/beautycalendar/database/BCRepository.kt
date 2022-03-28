package pl.dev.beautycalendar.database

import android.util.Log
import com.google.firebase.database.DataSnapshot
import pl.dev.beautycalendar.MainActivity
import pl.dev.beautycalendar.data.CustomerInfo
import pl.dev.beautycalendar.data.VisitsDate

class BCRepository {

    fun cancelVisit(customerInfo: CustomerInfo) {
        MainActivity.reference.child(customerInfo.telephone).setValue(customerInfo)
    }

    fun insertCustomer(customerInfo: CustomerInfo){
        MainActivity.reference.child(customerInfo.telephone).setValue(customerInfo)
    }

    fun setCustomersList(snapshot: DataSnapshot){
        Log.e("TAG", "Check")
        MainActivity.customersList.clear()
        snapshot.children.forEach { customer ->

            val listOfItems: ArrayList<Any> = ArrayList()

            customer.children.forEach {
                listOfItems.add(it.value!!)
            }

            val singleVisitsDate: ArrayList<VisitsDate> = ArrayList()

            val visitsDateRow = listOfItems[1] as ArrayList<*>

            visitsDateRow.forEach {
                val visitsDateRowMap = it as HashMap<*, *>
                val date = visitsDateRowMap["date"] as Long
                val service = visitsDateRowMap["service"] as String
                val visitsDate = VisitsDate(date, service)
                singleVisitsDate.add(visitsDate)
            }

            val singleCustomer = CustomerInfo(
                listOfItems[0].toString().toInt(),
                singleVisitsDate,
                listOfItems[2] as String,
                listOfItems[3] as String,
                listOfItems[4] as String,
            )

            MainActivity.customersList.add(singleCustomer)
        }

    }


}