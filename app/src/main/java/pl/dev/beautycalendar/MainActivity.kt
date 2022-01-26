package pl.dev.beautycalendar

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pl.dev.beautycalendar.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {



    companion object{
        var userName: String = ""
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
        }

        binding.otherLinearLayout.setOnClickListener{
            Log.e("TAG", "Other")
        }
    }


    private fun setViewPager(){

    }

    private fun setCalendar(){

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