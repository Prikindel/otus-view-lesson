package ru.otus.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.otus.views.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.chart.setValues(listOf(60, 20, 40, 80, 45, 30, 70))
    }
}