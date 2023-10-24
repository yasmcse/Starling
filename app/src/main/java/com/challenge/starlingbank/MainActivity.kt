package com.challenge.starlingbank

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity(R.layout.activity_main) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fragmentContainer =
            supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NavHostFragment
        val navigationControler = fragmentContainer.findNavController()
    }
}
