package pl.dev.beautycalendar

import android.R.layout
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import com.google.firebase.database.*
import pl.dev.beautycalendar.classes.MakeMessage
import pl.dev.beautycalendar.classes.ScheduleMessage
import pl.dev.beautycalendar.data.CustomerInfo
import pl.dev.beautycalendar.data.VisitsDate
import pl.dev.beautycalendar.databinding.ActivityOldCustomerBinding
import java.util.*
import kotlin.collections.ArrayList

class OldCustomerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOldCustomerBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private lateinit var listOfCustomers: ArrayList<CustomerInfo>
    private lateinit var makeMessage: MakeMessage
    private lateinit var scheduleMessage: ScheduleMessage

    private var phoneNumber = ""
    private var textMessage = ""
    private var dateTimeOfVisitMill = 0L
    private var messageId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOldCustomerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        makeMessage = MakeMessage()
        scheduleMessage = ScheduleMessage()

        database = FirebaseDatabase.getInstance()
        reference = database.getReference(MainActivity.userName)

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listOfCustomers = ArrayList()

                snapshot.children.forEach { customer ->

                    val listOfItems: ArrayList<Any> = ArrayList()

                    customer.children.forEach{
                        listOfItems.add(it.value!!)
                    }

                    val singleVisitsDate: ArrayList<VisitsDate> = ArrayList()

                    val visitsDateRow = listOfItems[1] as ArrayList<*>

                    visitsDateRow.forEach{
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

                    listOfCustomers.add(singleCustomer)
                }

                setAutoCompletedInfo()

            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "Failed to read value.", error.toException())
            }
        })

        setView()

    }


    private fun setView() {
        binding.oldCustomerTimePicker.setIs24HourView(true)

        binding.addOldCustomerButton.setOnClickListener {

            val customerName = binding.customerSpinner.text.toString()
            var service = binding.serviceEditText.text.toString()

            if(service.isBlank()){
                if(binding.serviceEditText.hint.toString().isNotBlank()){
                    service = binding.serviceEditText.hint.toString()
                }
            }

            val dateOfVisit = getDateOfVisitMillis()

            val customerId = getCustomerId(customerName)

            if (customerName.isNotBlank() && service.isNotBlank() && customerId.isNotBlank()) {
                addNewVisit(getCustomer(customerId, service, dateOfVisit))
            } else {
                Toast.makeText(this, "Wypełnij wszystkie pola", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun getCustomerId(customerName: String): String {

        val indexOfFirstSpace = customerName.indexOf(" ")

        return customerName.substring(0, indexOfFirstSpace)
    }

    private fun addNewVisit(customer: CustomerInfo) {

        reference.child(customer.telephone).setValue(customer)

        Toast.makeText(applicationContext, "Dodano wizytę", Toast.LENGTH_SHORT).show()

        phoneNumber = "+48" + customer.telephone
        textMessage = makeMessage.getMessage(customer)
        messageId = (customer.dateOf[customer.dateOf.size-1].date/1000/60).toInt() + customer.telephone.toInt()
        dateTimeOfVisitMill = customer.dateOf[customer.dateOf.size-1].date

        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.SEND_SMS
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            scheduleMessage.setScheduleMessage(applicationContext, this, textMessage, phoneNumber, messageId, dateTimeOfVisitMill)

        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.SEND_SMS),
                100
            )
        }

        this.finish()
    }




    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            scheduleMessage.setScheduleMessage(applicationContext, this, textMessage, phoneNumber, messageId, dateTimeOfVisitMill)
        } else {
            Toast.makeText(applicationContext, "Nie zezwolno na wysyłanie sms", Toast.LENGTH_SHORT)
                .show()
        }
    }


    private fun getCustomer(customerId: String, service: String, dateOfVisit: Long): CustomerInfo {

        val emptyList: ArrayList<VisitsDate> = ArrayList()
        var customerNewVisit = CustomerInfo(1, emptyList , "", "", "")

        listOfCustomers.forEach {
            if (it.telephone == customerId) {
                customerNewVisit = it
            }
        }


        val newVisitDate = VisitsDate(dateOfVisit, service)

        customerNewVisit.active = 1
        customerNewVisit.dateOf.add(newVisitDate)
        return customerNewVisit

    }

    private fun setAutoCompletedInfo(){
        val customersNameList = getCustomersNameList()
        val adapter = ArrayAdapter(applicationContext, layout.simple_list_item_1, customersNameList)
        binding.customerSpinner.setAdapter(adapter)

        binding.customerSpinner.doAfterTextChanged {
            if (it != null) {
                if (it.length > 9) {
                   setLastService()
                }
            }
        }

    }

    private fun setLastService() {

        val customerName = binding.customerSpinner.text.toString()
        val customerId = getCustomerId(customerName)

        val emptyList: ArrayList<VisitsDate> = ArrayList()
        var customer = CustomerInfo(1, emptyList , "", "", "")

        listOfCustomers.forEach {
            if (it.telephone == customerId) {
                customer = it
            }
        }

        val lastService = customer.dateOf[customer.dateOf.size-1].service

        binding.serviceEditText.hint = lastService

    }


    private fun getCustomersNameList(): ArrayList<String> {

        val customersNameList: ArrayList<String> = ArrayList()

        listOfCustomers.forEach {
            customersNameList.add("${it.telephone} ${it.name} ${it.surname}")
        }

        return customersNameList
    }

    private fun getDateOfVisitMillis(): Long {

        val year = binding.oldCustomerDatePicker.year
        val month = binding.oldCustomerDatePicker.month
        val day = binding.oldCustomerDatePicker.dayOfMonth
        val hour = binding.oldCustomerTimePicker.hour
        val minute = binding.oldCustomerTimePicker.minute

        val calendar = Calendar.getInstance()
        calendar.set(year, month, day, hour, minute)

        return calendar.timeInMillis
    }


}