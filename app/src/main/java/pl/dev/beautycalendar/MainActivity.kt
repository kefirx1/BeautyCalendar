package pl.dev.beautycalendar

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.applandeo.materialcalendarview.EventDay
import com.google.firebase.database.FirebaseDatabase
import pl.dev.beautycalendar.adapter.EventsAdapter
import pl.dev.beautycalendar.adapter.ViewPagerUpcomingAdapter
import pl.dev.beautycalendar.classes.DeviceInfo
import pl.dev.beautycalendar.data.CustomerInfo
import pl.dev.beautycalendar.classes.FirebaseData
import pl.dev.beautycalendar.databinding.ActivityMainBinding
import pl.dev.beautycalendar.viewModel.BCViewModel
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object {
        var userName = ""
        val customersList: ArrayList<CustomerInfo> = ArrayList()
        val database = FirebaseDatabase.getInstance()
        var reference = database.getReference(userName)
        var isOnline = true
        lateinit var viewModel: BCViewModel
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseData: FirebaseData

    private val customerToEvent: HashMap<EventDay, CustomerInfo> = HashMap()
    private val events: ArrayList<EventDay> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseData = FirebaseData()
        setUserName()

        var driver="com.mysql.cj.jdbc.Driver"
        var url="jdbc:mysql://mysql.ct8.pl:3306/m25103_beautyCalendar"
        var user="m25103_root"
        var psd="Baziucha12345@"
        try {
            Class.forName(driver).newInstance()
            println("Connected successfully 1")
        }catch (e:Exception){
            e.printStackTrace()
            println("Connection failed")
        }

        try{
            val conn=DriverManager.getConnection(url,user,psd)
            var sql:String="insert into stuinfo(id,name)values(?,?)"
            val ps: PreparedStatement =conn.prepareStatement(sql)
            ps.setString(1,"3")
            ps.setString(2,"vocus")
            ps.execute()


        }catch (e:Exception) {
            e.printStackTrace()
            println("Connection failed 2")
        }





    }

    override fun onResume() {
        super.onResume()

        isOnline = DeviceInfo.isDeviceOnline(applicationContext)

        viewModel = ViewModelProvider
            .AndroidViewModelFactory
            .getInstance(application)
            .create(BCViewModel::class.java)

        Log.d("TAG", "On resume")

        clearEvents()

        if (userName != "") {
            reference = database.getReference(userName)
            firebaseData.getListOfVisits(this)
            setListeners()
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

        binding.customersListButton.setOnClickListener {
            Log.e("TAG", "Customers list")
            resetModals()
            val intent = Intent(this, CustomersListActivity::class.java)
            startActivity(intent)
        }
    }

    private fun resetModals() {
        binding.newVisitModal.visibility = View.GONE
        binding.eventVisitsModal.visibility = View.GONE
    }


    fun setViewPager() {

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
            ViewPagerUpcomingAdapter(customerUpcomingList)
        binding.upcomingCircleIndicator3.setViewPager(binding.upcomingVisitsViewPager)

    }


    fun clearEvents() {
        customersList.clear()
        customerToEvent.clear()
        events.clear()
    }


    fun setCalendar() {

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