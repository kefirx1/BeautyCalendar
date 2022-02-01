package pl.dev.beautycalendar

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import pl.dev.beautycalendar.classes.DateOfVisits
import pl.dev.beautycalendar.classes.MakeMessage
import pl.dev.beautycalendar.classes.ScheduleMessage
import pl.dev.beautycalendar.data.CustomerInfo
import pl.dev.beautycalendar.data.VisitsDate
import pl.dev.beautycalendar.databinding.ActivityNewCustomerBinding
import java.util.*

class NewCustomerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewCustomerBinding
    private lateinit var newCustomer: CustomerInfo
    private lateinit var makeMessage: MakeMessage
    private lateinit var scheduleMessage: ScheduleMessage
    private lateinit var dateOfVisits: DateOfVisits
    private var phoneNumber = ""
    private var textMessage = ""
    private var dateTimeOfVisitMill = 0L
    private var messageId = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNewCustomerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        scheduleMessage = ScheduleMessage()
        makeMessage = MakeMessage()
        dateOfVisits = DateOfVisits()

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

            val dateOfVisit = dateOfVisits.getDateOfVisitMillis(binding.newCustomerDatePicker, binding.newCustomerTimePicker)

            val visitsDate = VisitsDate(dateOfVisit, service)
            val visitsDateList: ArrayList<VisitsDate> = ArrayList()
            visitsDateList.add(visitsDate)


            if (name.isNotBlank() && surname.isNotBlank() && telephone.isNotBlank() && service.isNotBlank()) {
                newCustomer = CustomerInfo(1, visitsDateList, name, surname, telephone)
                addNewVisit(newCustomer)
            } else {
                Toast.makeText(this, "Wypełnij wszystkie pola", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addNewVisit(newCustomer: CustomerInfo) {

        MainActivity.reference.child(newCustomer.telephone).setValue(newCustomer)

        Toast.makeText(applicationContext, "Dodano wizytę", Toast.LENGTH_SHORT).show()

        phoneNumber = "+48" + newCustomer.telephone
        textMessage = makeMessage.getMessageString(newCustomer)
        messageId = makeMessage.getMessageId(newCustomer)
        dateTimeOfVisitMill = newCustomer.dateOf[0].date


        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.SEND_SMS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            scheduleMessage.setScheduleMessage(
                applicationContext,
                this,
                textMessage,
                phoneNumber,
                messageId,
                dateTimeOfVisitMill
            )
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