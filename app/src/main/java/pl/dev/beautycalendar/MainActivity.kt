package pl.dev.beautycalendar

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.applandeo.materialcalendarview.EventDay
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import pl.dev.beautycalendar.databinding.ActivityMainBinding
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {



    companion object{
        var userName = ""
        val customerInfo = CustomerInfo("Jan", "Kowalski", "123456789", "Paznokcie",1643316694000)
        val customerInfo2 = CustomerInfo("Jan", "Kowalski", "123456789", "Paznokcie",1643403094000)

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


        customersList.add(customerInfo)
        customersList.add(customerInfo2)








//        binding.sendMessageButton.setOnClickListener {
//
//            if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED){
//                sendMessage()
//            }else {
//                ActivityCompat.requestPermissions(
//                    this,
//                    arrayOf(android.Manifest.permission.SEND_SMS),
//                    100
//                )
//            }
//        }

    }


    override fun onResume() {
        super.onResume()

        setViewPager()
        setCalendar()




    }


    private fun setListeners(){
        binding.newVisitButton.setOnClickListener{
            Log.e("TAG", "New visit")
            val database = FirebaseDatabase.getInstance()

            val reference = database.getReference("monika")


            reference.child(customerInfo.date.toString()).setValue(customerInfo)


            Toast.makeText(applicationContext, "Dupa", Toast.LENGTH_SHORT).show()
        }

        binding.otherLinearLayout.setOnClickListener{
            Log.e("TAG", "Other")
        }
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








    private fun sendMessage(){
        val smsManager = SmsManager.getDefault()
        smsManager.sendTextMessage(phoneNumber, null, textMessage, null, null)

        Toast.makeText(applicationContext, "Wysłano!!", Toast.LENGTH_SHORT).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            sendMessage()
        }else{
            Toast.makeText(applicationContext, "Nie zezwolno na wysyłanie sms", Toast.LENGTH_SHORT).show()
        }
    }

}