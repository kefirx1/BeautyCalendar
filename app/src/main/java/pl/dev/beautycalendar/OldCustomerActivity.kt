package pl.dev.beautycalendar

import android.R.layout
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import pl.dev.beautycalendar.classes.DateOfVisits
import pl.dev.beautycalendar.classes.FirebaseData
import pl.dev.beautycalendar.classes.MakeMessage
import pl.dev.beautycalendar.classes.ScheduleMessage
import pl.dev.beautycalendar.data.CustomerInfo
import pl.dev.beautycalendar.data.VisitsDate
import pl.dev.beautycalendar.databinding.ActivityOldCustomerBinding
import java.util.*
import kotlin.collections.ArrayList

class OldCustomerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOldCustomerBinding
    private lateinit var makeMessage: MakeMessage
    private lateinit var scheduleMessage: ScheduleMessage
    private lateinit var firebaseData: FirebaseData
    private lateinit var dateOfVisits: DateOfVisits

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
        firebaseData = FirebaseData()
        dateOfVisits = DateOfVisits()

        firebaseData.getListOfVisits(this)


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

            val dateOfVisit = dateOfVisits.getDateOfVisitMillis(binding.oldCustomerDatePicker, binding.oldCustomerTimePicker)

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

        MainActivity.reference.child(customer.telephone).setValue(customer)

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

        MainActivity.customersList.forEach {
            if (it.telephone == customerId) {
                customerNewVisit = it
            }
        }


        val newVisitDate = VisitsDate(dateOfVisit, service)

        customerNewVisit.active = 1
        customerNewVisit.dateOf.add(newVisitDate)
        return customerNewVisit

    }

    fun setAutoCompletedInfo(){
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

        MainActivity.customersList.forEach {
            if (it.telephone == customerId) {
                customer = it
            }
        }

        val lastService = customer.dateOf[customer.dateOf.size-1].service

        binding.serviceEditText.hint = lastService

    }


    private fun getCustomersNameList(): ArrayList<String> {

        val customersNameList: ArrayList<String> = ArrayList()

        MainActivity.customersList.forEach {
            customersNameList.add("${it.telephone} ${it.name} ${it.surname}")
        }

        return customersNameList
    }

}