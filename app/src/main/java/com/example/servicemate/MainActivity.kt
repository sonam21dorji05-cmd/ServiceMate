package com.example.servicemate

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.servicemate.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Navigation is fully handled by the NavHostFragment declared in activity_main.xml
        // (start destination = loginFragment, see res/navigation/nav_graph.xml)
    }
}