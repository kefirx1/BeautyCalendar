package pl.dev.beautycalendar

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.database.FirebaseDatabase
import pl.dev.beautycalendar.MainActivity.Companion.userName
import pl.dev.beautycalendar.classes.MakeMessage
import pl.dev.beautycalendar.classes.ScheduleMessage
import pl.dev.beautycalendar.data.CustomerInfo
import pl.dev.beautycalendar.data.VisitsDate
import pl.dev.beautycalendar.databinding.ActivityNewCustomerBinding
import java.util.*
import kotlin.collections.ArrayList

class NewCustomerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewCustomerBinding
    private lateinit var newCustomer: CustomerInfo
    private lateinit var makeMessage: MakeMessage
    private lateinit var scheduleMessage: ScheduleMessage
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
            val dateOfVisit = getDateOfVisitMillis()

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

        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference(userName)

        reference.child(newCustomer.telephone).setValue(newCustomer)

        Toast.makeText(applicationContext, "Dodano wizytę", Toast.LENGTH_SHORT).show()

        phoneNumber = "+48" + newCustomer.telephone
        textMessage = makeMessage.getMessage(newCustomer)
        messageId = (newCustomer.dateOf[0].date/1000/60).toInt() + newCustomer.telephone.toInt()
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


    private fun getDateOfVisitMillis(): Long {

        val year = binding.newCustomerDatePicker.year
        val month = binding.newCustomerDatePicker.month
        val day = binding.newCustomerDatePicker.dayOfMonth
        val hour = binding.newCustomerTimePicker.hour
        val minute = binding.newCustomerTimePicker.minute

        val calendar = Calendar.getInstance()
        calendar.set(year, month, day, hour, minute)

        return calendar.timeInMillis
    }


}