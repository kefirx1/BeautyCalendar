package pl.dev.beautycalendar

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.applandeo.materialcalendarview.EventDay
import com.google.firebase.database.*
import pl.dev.beautycalendar.adapter.ViewPagerAdapter
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
        val customerToEvent: HashMap<EventDay, CustomerInfo> = HashMap()
        val events: ArrayList<EventDay> = ArrayList()
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
            setCalendarEventsListener()
        }

    }

    private fun setCalendarEventsListener() {
        binding.calendarView.setOnDayClickListener{ eventDay ->

            resetModals()

            val visitsViewList: ArrayList<CustomerInfo> = ArrayList()

            if(events.contains(eventDay)){
                binding.eventCircleIndicator3.visibility = View.VISIBLE
                binding.eventVisitsModal.visibility = View.VISIBLE

                customerToEvent.forEach{
                    if(it.value.date / 1000 / 60 / 60 / 24 == (eventDay.calendar.timeInMillis / 1000 / 60 / 60 / 24 )+ 1){
                        visitsViewList.add(it.value)
                    }
                }

                visitsViewList.sortBy {
                    it.date
                }

                binding.viewPagerEventVisits.adapter =
                    ViewPagerAdapter(visitsViewList, applicationContext, this)
                binding.eventCircleIndicator3.setViewPager(binding.viewPagerEventVisits)

            }else{
                visitsViewList.clear()
                binding.eventCircleIndicator3.visibility = View.GONE
                binding.eventVisitsModal.visibility = View.GONE
            }
        }
    }


    private fun setListeners() {
        binding.eventVisitsModalBackButton.setOnClickListener{
            binding.eventVisitsModal.visibility = View.GONE
        }

        binding.newVisitButton.setOnClickListener {
            Log.e("TAG", "New visit")

            resetModals()

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
        binding.eventVisitsModal.visibility = View.GONE
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
                        singleCustomerInfoList[0].toInt(),
                        singleCustomerInfoList[1].toLong(),
                        singleCustomerInfoList[2],
                        singleCustomerInfoList[3],
                        singleCustomerInfoList[4],
                        singleCustomerInfoList[5]
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
        customerToEvent.clear()
        events.clear()
    }



    private fun setCalendar() {

        Log.e("TAG", "Calendar")

        val calendar = Calendar.getInstance()

        customersList.forEach {
            if(it.active==1){
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
        }

        binding.calendarView.setEvents(events)

    }


    private fun setUserName() {
        resetModals()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }


}