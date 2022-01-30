package pl.dev.beautycalendar

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.applandeo.materialcalendarview.EventDay
import com.google.firebase.database.*
import pl.dev.beautycalendar.data.CustomerInfo
import pl.dev.beautycalendar.databinding.ActivityMainBinding
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {


    companion object {
        var userName = ""
        val customersList: ArrayList<CustomerInfo> = ArrayList()
        val customersToViewList: ArrayList<CustomerInfo> = ArrayList()
        val customerToEvent: HashMap<EventDay, CustomerInfo> = HashMap()
        val events: MutableList<EventDay> = ArrayList()
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUserName()

    }

    override fun onResume() {
        super.onResume()

        Log.d("TAG", "On resume")

        clearEvents()

        if (userName != "") {
            setListeners()

            checkVisits()
            setViewPager()
            setCalendar()

        }

    }


    private fun setListeners() {
        binding.newVisitButton.setOnClickListener {
            Log.e("TAG", "New visit")
            binding.newVisitModal.visibility = View.VISIBLE

            binding.newVisitModalBackButton.setOnClickListener {
                binding.newVisitModal.visibility = View.GONE
            }

            binding.newCustomerButton.setOnClickListener {
                Log.e("TAG", "New customer")
                val intent = Intent(this, NewCustomerActivity::class.java)
                resetModals()
                startActivity(intent)
            }

            binding.oldCustomerButton.setOnClickListener {
                Log.e("TAG", "Old customer")
                val intent = Intent(this, OldCustomerActivity::class.java)
                resetModals()
                startActivity(intent)
            }


        }

        binding.otherLinearLayout.setOnClickListener {
            Log.e("TAG", "Other")
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun resetModals() {
        binding.newVisitModal.visibility = View.GONE
    }


    private fun setViewPager() {

    }


    private fun checkVisits() {
        database = FirebaseDatabase.getInstance()
        reference = database.getReference(userName)

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                clearEvents()
                val singleCustomerInfoList: java.util.ArrayList<String> = java.util.ArrayList()

                snapshot.children.forEach { customer ->
                    customer.children.forEach {
                        singleCustomerInfoList.add(it.value.toString())
                    }
                    val singleCustomer = CustomerInfo(
                        singleCustomerInfoList[0].toLong(),
                        singleCustomerInfoList[1],
                        singleCustomerInfoList[2],
                        singleCustomerInfoList[3],
                        singleCustomerInfoList[4]
                    )
                    customersList.add(singleCustomer)
                    singleCustomerInfoList.clear()
                }
                setCalendar()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "Failed to read value.", error.toException())
            }
        })
    }

    private fun clearEvents(){
        customersList.clear()
        customersToViewList.clear()
        customerToEvent.clear()
        events.clear()
    }



    private fun setCalendar() {

        Log.e("TAG", "Calendar")

        val calendar = Calendar.getInstance()

        customersList.forEach {
            customersToViewList.add(it)
            val dateOfVisitMillis = it.date
            val dateOfVisitSec = dateOfVisitMillis / 1000
            val dateOfVisit = LocalDateTime.ofEpochSecond(
                dateOfVisitSec, 0,
                ZoneOffset.UTC
            )

            var hour = dateOfVisit.hour + 1

            if (dateOfVisit.monthValue == 3 && dateOfVisit.dayOfMonth >= 27) {
                hour++
            } else if (dateOfVisit.monthValue in 4..8) {
                hour++
            }

            calendar.set(
                dateOfVisit.year,
                dateOfVisit.monthValue - 1,
                dateOfVisit.dayOfMonth,
                hour,
                dateOfVisit.minute,
                dateOfVisit.second
            )
            val event = EventDay(calendar.clone() as Calendar, R.drawable.ic_baseline_event_24)
            events.add(event)
            customerToEvent[event] = it
        }

        binding.calendarView.setEvents(events)

    }


    private fun setUserName() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }


}