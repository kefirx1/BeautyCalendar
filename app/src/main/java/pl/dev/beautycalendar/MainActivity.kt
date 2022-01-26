package pl.dev.beautycalendar

import android.app.Activity
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import pl.dev.beautycalendar.databinding.ActivityMainBinding
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding


    val phoneNumber = "+48605386566"
    val textMessage = "Test"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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