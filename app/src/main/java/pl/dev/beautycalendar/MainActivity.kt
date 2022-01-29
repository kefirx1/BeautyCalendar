package pl.dev.beautycalendar

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.applandeo.materialcalendarview.EventDay
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

    val phoneNumber = "+48605386566"
    val textMessage = "Test"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUserName()
        setListeners()


    }


    override fun onResume() {
        super.onResume()


        setViewPager()
        setCalendar()




    }


    private fun setListeners(){
        binding.newVisitButton.setOnClickListener{
            Log.e("TAG", "New visit")
            binding.newVisitModal.visibility = View.VISIBLE

            binding.newVisitModalBackButton.setOnClickListener{
                binding.newVisitModal.visibility = View.GONE
            }

            binding.newCustomerButton.setOnClickListener{
                Log.e("TAG", "New customer")
                val intent = Intent(this, NewCustomerActivity::class.java)
                resetModals()
                startActivity(intent)
            }

            binding.oldCustomerButton.setOnClickListener{
                Log.e("TAG", "Old customer")
                val intent = Intent(this, OldCustomerActivity::class.java)
                resetModals()
                startActivity(intent)
            }


        }

        binding.otherLinearLayout.setOnClickListener{
            Log.e("TAG", "Other")
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun resetModals() {
        binding.newVisitModal.visibility = View.GONE
    }


    private fun setViewPager(){

    }



    private fun setCalendar(){

        val calendar = Calendar.getInstance()

        customersList.forEach{
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
            val event = EventDay(calendar.clone() as Calendar, R.drawable.ic_baseline_add_24)
            events.add(event)
            customerToEvent[event] = it
        }

        binding.calendarView.setEvents(events)

    }


    private fun setUserName(){
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }


}