package pl.dev.beautycalendar

import android.R.layout
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import pl.dev.beautycalendar.classes.*
import pl.dev.beautycalendar.data.CustomerInfo
import pl.dev.beautycalendar.data.VisitsDate
import pl.dev.beautycalendar.classes.FirebaseData
import pl.dev.beautycalendar.databinding.ActivityOldCustomerBinding

class OldCustomerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOldCustomerBinding
    private lateinit var scheduleMessage: ScheduleMessage
    private lateinit var firebaseData: FirebaseData
    private lateinit var newVisit: NewVisit

    private var phoneNumber = ""
    private var textMessage = ""
    private var dateTimeOfVisitMill = 0L
    private var messageId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOldCustomerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        scheduleMessage = ScheduleMessage()
        firebaseData = FirebaseData()
        newVisit = NewVisit()

        firebaseData.getListOfVisits(this)


        setView()

    }

    private fun setView() {
        binding.oldCustomerTimePicker.setIs24HourView(true)

        binding.addOldCustomerButton.setOnClickListener {

            val customerName = binding.customerSpinner.text.toString()
            var service = binding.serviceEditText.text.toString()

            if (service.isBlank()) {
                if (binding.serviceEditText.hint.toString().isNotBlank()) {
                    service = binding.serviceEditText.hint.toString()
                }
            }

            val dateOfVisit = DateOfVisits.getDateOfVisitMillis(
                binding.oldCustomerDatePicker,
                binding.oldCustomerTimePicker
            )

            val customerId = Customer.getCustomerId(customerName)

            if (customerName.isNotBlank() && service.isNotBlank() && customerId.isNotBlank()) {
                newVisit.addNewVisit(
                    Customer.getCustomer(customerId, service, dateOfVisit),
                    this,
                    applicationContext
                )
            } else {
                Toast.makeText(this, "Wypełnij wszystkie pola", Toast.LENGTH_SHORT).show()
            }

        }
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

    fun setAutoCompletedInfo() {
        val customersNameList = Customer.getCustomersNameList()
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
        val customerId = Customer.getCustomerId(customerName)

        val emptyList: ArrayList<VisitsDate> = ArrayList()
        var customer = CustomerInfo(1, emptyList, "", "", "")

        MainActivity.customersList.forEach {
            if (it.telephone == customerId) {
                customer = it
            }
        }

        val lastService = customer.dateOf[customer.dateOf.size - 1].service

        binding.serviceEditText.hint = lastService

    }

}