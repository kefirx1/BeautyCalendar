package pl.dev.beautycalendar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import pl.dev.beautycalendar.MainActivity.Companion.customerInfo
import pl.dev.beautycalendar.MainActivity.Companion.userName
import pl.dev.beautycalendar.databinding.ActivityNewCustomerBinding
import java.util.*

class NewCustomerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewCustomerBinding
    private lateinit var newCustomer: CustomerInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNewCustomerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setView()
    }


    private fun setView(){
        binding.newCustomerTimePicker.setIs24HourView(true)

        binding.addNewCustomerButton.setOnClickListener{

            val name = binding.nameEditText.text.toString()
            val surname = binding.surnameEditText.text.toString()
            val telephone = binding.telephoneEditText.text.toString()
            val service = binding.serviceEditText.text.toString()
            val dateOfVisit = getDateOfVisitMillis()

            newCustomer = CustomerInfo(dateOfVisit, service, surname, name, telephone)

            addNewVisit(newCustomer)

        }
    }

    private fun addNewVisit(newCustomer: CustomerInfo) {

        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference(userName)

        reference.child(newCustomer.telephone).setValue(newCustomer)

        Toast.makeText(applicationContext, "Dodano wizytę", Toast.LENGTH_SHORT).show()

        this.finish()
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