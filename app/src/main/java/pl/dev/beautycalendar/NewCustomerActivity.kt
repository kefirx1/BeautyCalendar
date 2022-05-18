package pl.dev.beautycalendar

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pl.dev.beautycalendar.classes.DateTimeConverter
import pl.dev.beautycalendar.classes.NewVisit
import pl.dev.beautycalendar.classes.ScheduleMessage
import pl.dev.beautycalendar.data.CustomerInfo
import pl.dev.beautycalendar.data.VisitsDate
import pl.dev.beautycalendar.databinding.ActivityNewCustomerBinding
import java.util.*

class NewCustomerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewCustomerBinding
    private lateinit var newCustomer: CustomerInfo
    private lateinit var scheduleMessage: ScheduleMessage
    private lateinit var newVisit: NewVisit
    private var phoneNumber = ""
    private var textMessage = ""
    private var dateTimeOfVisitMill = 0L
    private var messageId = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNewCustomerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        scheduleMessage = ScheduleMessage()
        newVisit = NewVisit()

        setView()
    }

    private fun setView() {
        binding.newCustomerTimePicker.setIs24HourView(true)

        binding.addNewCustomerButton.setOnClickListener {

            val name = binding.nameEditText.text.toString()
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
            val surname = binding.surnameEditText.text.toString()
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
            val telephone = binding.telephoneEditText.text.toString()
            val service = binding.serviceEditText.text.toString()

            val dateOfVisit = DateTimeConverter.getDateOfVisitMillis(
                binding.newCustomerDatePicker,
                binding.newCustomerTimePicker
            )

            val visitsDate = VisitsDate(dateOfVisit, service)
            val visitsDateList: ArrayList<VisitsDate> = ArrayList()
            visitsDateList.add(visitsDate)


            if (name.isNotBlank() && surname.isNotBlank() && telephone.isNotBlank() && service.isNotBlank()) {
                newCustomer = CustomerInfo(1, visitsDateList, name, surname, telephone)
                newVisit.addNewVisit(newCustomer, this, applicationContext)
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
            scheduleMessage.setScheduleMessage(
                applicationContext,
                this,
                textMessage,
                phoneNumber,
                messageId,
                dateTimeOfVisitMill
            )
        } else {
            Toast.makeText(applicationContext, "Nie zezwolno na wysyłanie sms", Toast.LENGTH_SHORT)
                .show()
        }
    }


}