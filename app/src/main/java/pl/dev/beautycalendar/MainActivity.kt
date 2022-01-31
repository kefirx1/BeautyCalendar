package pl.dev.beautycalendar

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.applandeo.materialcalendarview.EventDay
import com.google.firebase.database.*
import pl.dev.beautycalendar.adapter.EventsAdapter
import pl.dev.beautycalendar.adapter.ViewPagerUpcomingAdapter
import pl.dev.beautycalendar.data.CustomerInfo
import pl.dev.beautycalendar.data.VisitsDate
import pl.dev.beautycalendar.databinding.ActivityMainBinding
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

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
        binding.calendarView.setOnDayClickListener { eventDay ->

            resetModals()

            val visitsViewList: ArrayList<CustomerInfo> = ArrayList()

            if (events.contains(eventDay)) {
                binding.eventVisitsModal.visibility = View.VISIBLE

                customerToEvent.forEach {
                    if (it.value.dateOf[it.value.dateOf.size - 1].date / 1000 / 60 / 60 / 24 == (eventDay.calendar.timeInMillis / 1000 / 60 / 60 / 24) + 1) {
                        visitsViewList.add(it.value)
                    }
                }

                val adapter = EventsAdapter(visitsViewList, applicationContext, this)

                binding.eventRecyclerView.adapter = adapter
                binding.eventRecyclerView.layoutManager = LinearLayoutManager(this)


            } else {
                visitsViewList.clear()
                binding.eventVisitsModal.visibility = View.GONE
            }
        }
    }


    private fun setListeners() {
        binding.eventVisitsModalBackButton.setOnClickListener {
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

        binding.changeUserButton.setOnClickListener {
            Log.e("TAG", "Other")
            setUserName()
        }
    }

    private fun resetModals() {
        binding.newVisitModal.visibility = View.GONE
        binding.eventVisitsModal.visibility = View.GONE
    }


    private fun setViewPager() {

        val currentDay = Calendar.getInstance()
        val currentDayEnd = Calendar.getInstance()
        val currentDayOfMonth = currentDay.get(Calendar.DAY_OF_MONTH)
        val currentMonth = currentDay.get(Calendar.MONTH)
        val currentYear = currentDay.get(Calendar.YEAR)
        val currentHour = currentDay.get(Calendar.HOUR_OF_DAY)


        currentDay.set(currentYear, currentMonth, currentDayOfMonth, 0, 0, 0)
        currentDayEnd.set(currentYear, currentMonth, currentDayOfMonth, 23, 59, 59)

        val currentDayStartMill = currentDay.timeInMillis
        val currentDayEndMill = currentDayEnd.timeInMillis

        currentDay.set(currentYear, currentMonth, currentDayOfMonth + 1, 0, 0, 0)
        currentDayEnd.set(currentYear, currentMonth, currentDayOfMonth + 1, 23, 59, 59)

        val nextDayStartMill = currentDay.timeInMillis
        val nextDayEndMill = currentDayEnd.timeInMillis

        val customerUpcomingList: ArrayList<CustomerInfo> = ArrayList()

        customersList.forEach { customer ->

            if (customer.active == 1) {

                if (currentHour < 19) {
                    if (customer.dateOf[customer.dateOf.size - 1].date in currentDayStartMill..currentDayEndMill) {
                        customerUpcomingList.add(customer)
                    }
                } else {
                    if (customer.dateOf[customer.dateOf.size - 1].date in nextDayStartMill..nextDayEndMill) {
                        customerUpcomingList.add(customer)
                    }
                }
            }

        }

        binding.upcomingVisitsViewPager.adapter =
            ViewPagerUpcomingAdapter(customerUpcomingList, applicationContext, this)
        binding.upcomingCircleIndicator3.setViewPager(binding.upcomingVisitsViewPager)

    }


    private fun checkVisits() {
        database = FirebaseDatabase.getInstance()
        reference = database.getReference(userName)

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                clearEvents()

                Log.e("TAG", "Check")


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

                    customersList.add(singleCustomer)
                }
                setViewPager()
                setCalendar()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "Failed to read value.", error.toException())
            }
        })
    }

    private fun clearEvents() {
        customersList.clear()
        customerToEvent.clear()
        events.clear()
    }


    private fun setCalendar() {

        Log.e("TAG", "Calendar")

        val calendar = Calendar.getInstance()

        customersList.forEach {
            if (it.active == 1) {
                val dateOfVisitMillis = it.dateOf[it.dateOf.size - 1].date
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