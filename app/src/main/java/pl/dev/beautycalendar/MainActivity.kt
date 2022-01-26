package pl.dev.beautycalendar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import pl.dev.beautycalendar.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.sendMessageButton.setOnClickListener{
            Toast.makeText(applicationContext, "Siemka", Toast.LENGTH_SHORT).show()
        }

    }
}