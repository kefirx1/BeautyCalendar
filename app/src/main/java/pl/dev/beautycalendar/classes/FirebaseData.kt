package pl.dev.beautycalendar.classes

import android.app.Activity
import android.util.Log
import com.google.firebase.database.*
import pl.dev.beautycalendar.CustomersListActivity
import pl.dev.beautycalendar.MainActivity
import pl.dev.beautycalendar.OldCustomerActivity
import pl.dev.beautycalendar.data.CustomerInfo
import pl.dev.beautycalendar.data.VisitsDate
import java.util.ArrayList
import java.util.HashMap

class FirebaseData {



    fun getListOfVisits(instance: Activity) {

        when (instance.componentName.shortClassName) {
            ".MainActivity" -> doForMainActivity(instance as MainActivity)
            ".CustomersListActivity" -> doForCustomersListActivity(instance as CustomersListActivity)
            ".OldCustomerActivity" -> doForOldCustomerActivity(instance as OldCustomerActivity)
        }

    }

    private fun getFirebaseData(snapshot: DataSnapshot) {
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



    private fun doForMainActivity(instance: MainActivity) {

        MainActivity.reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                instance.clearEvents()
                getFirebaseData(snapshot)
                instance.setViewPager()
                instance.setCalendar()

            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "Failed to read value.", error.toException())
            }
        })

    }

    private fun doForOldCustomerActivity(instance: OldCustomerActivity) {

        MainActivity.reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                getFirebaseData(snapshot)
                instance.setAutoCompletedInfo()

            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "Failed to read value.", error.toException())
            }
        })
    }

    private fun doForCustomersListActivity(instance: CustomersListActivity) {

        MainActivity.reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                getFirebaseData(snapshot)
                instance.setAutoCompletedInfo()

            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "Failed to read value.", error.toException())
            }
        })
    }

}