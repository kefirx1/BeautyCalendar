package pl.dev.beautycalendar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import pl.dev.beautycalendar.databinding.ActivityOldCustomerBinding

class OldCustomerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOldCustomerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOldCustomerBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}