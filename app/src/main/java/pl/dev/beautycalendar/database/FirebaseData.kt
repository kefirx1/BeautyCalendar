package pl.dev.beautycalendar.database

import android.app.Activity
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import pl.dev.beautycalendar.CustomersListActivity
import pl.dev.beautycalendar.MainActivity
import pl.dev.beautycalendar.OldCustomerActivity

class FirebaseData {


    fun getListOfVisits(instance: Activity) {

        when (instance.componentName.shortClassName) {
            ".MainActivity" -> doForMainActivity(instance as MainActivity)
            ".CustomersListActivity" -> doForCustomersListActivity(instance as CustomersListActivity)
            ".OldCustomerActivity" -> doForOldCustomerActivity(instance as OldCustomerActivity)
        }

    }

    private fun doForMainActivity(instance: MainActivity) {

        MainActivity.reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                instance.clearEvents()
                MainActivity.viewModel.setCustomerList(snapshot)
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

                MainActivity.viewModel.setCustomerList(snapshot)
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

                MainActivity.viewModel.setCustomerList(snapshot)
                instance.setAutoCompletedInfo()

            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "Failed to read value.", error.toException())
            }
        })
    }

}