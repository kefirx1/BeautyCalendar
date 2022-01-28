package pl.dev.beautycalendar

import android.R.layout
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.database.*
import pl.dev.beautycalendar.databinding.ActivityOldCustomerBinding
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.sin

class OldCustomerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOldCustomerBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private lateinit var listOfCustomers: ArrayList<CustomerInfo>
    private lateinit var makeMessage: MakeMessage


    private var phoneNumber = ""
    private var textMessage = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOldCustomerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        makeMessage = MakeMessage()

        database = FirebaseDatabase.getInstance()
        reference = database.getReference(MainActivity.userName)

        reference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listOfCustomers = ArrayList()
                val singleCustomerInfoList: ArrayList<String> = ArrayList()

                snapshot.children.forEach{ customer ->
                    customer.children.forEach{
                        singleCustomerInfoList.add(it.value.toString())
                    }
                    val singleCustomer = CustomerInfo(singleCustomerInfoList[0].toLong(), singleCustomerInfoList[1], singleCustomerInfoList[2], singleCustomerInfoList[3], singleCustomerInfoList[4])
                    listOfCustomers.add(singleCustomer)
                    singleCustomerInfoList.clear()
                }
                setSpinnerInfo()
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "Failed to read value.", error.toException())
            }
        })

        setView()

    }


    private fun setView() {
        binding.oldCustomerTimePicker.setIs24HourView(true)




        binding.addOldCustomerButton.setOnClickListener{

            val customerName = binding.customerSpinner.selectedItem.toString()
            val service = binding.serviceEditText.text.toString()
            val dateOfVisit = getDateOfVisitMillis()

            val customerId = getCustomerId(customerName)

            if(customerName.isNotBlank() && service.isNotBlank() && customerId.isNotBlank()){
                addNewVisit(getCustomer(customerId, service, dateOfVisit))
            }else{
                Toast.makeText(this, "Wypełnij wszystkie pola", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun getCustomerId(customerName: String): String {

        val indexOfFirstSpace = customerName.indexOf(" ")

        return customerName.substring(0, indexOfFirstSpace)
    }

    private fun addNewVisit(customer: CustomerInfo){

        reference.child(customer.telephone).setValue(customer)

        Toast.makeText(applicationContext, "Dodano wizytę", Toast.LENGTH_SHORT).show()



        phoneNumber = "+48" + customer.telephone
        textMessage = makeMessage.getMessage(customer)

        println(textMessage)


        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED){
                sendMessage(phoneNumber, textMessage)
            }else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.SEND_SMS),
                    100
                )
            }

        this.finish()
    }


    private fun sendMessage(phoneNumber: String, textMessage: String){
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
            sendMessage(phoneNumber, textMessage)
        }else{
            Toast.makeText(applicationContext, "Nie zezwolno na wysyłanie sms", Toast.LENGTH_SHORT).show()
        }
    }



    private fun getCustomer(customerId: String, service: String, dateOfVisit: Long): CustomerInfo{

        var customerNewVisit = CustomerInfo(0, "", "", "", "")

        listOfCustomers.forEach{
            if(it.telephone == customerId){
                customerNewVisit = it
            }
        }

        customerNewVisit.service = service
        customerNewVisit.date = dateOfVisit

        return customerNewVisit

    }

    private fun setSpinnerInfo(){

        val customersNameList = getCustomersNameList()

        val adapter = ArrayAdapter(this, layout.simple_spinner_item, customersNameList)

        binding.customerSpinner.adapter = adapter

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