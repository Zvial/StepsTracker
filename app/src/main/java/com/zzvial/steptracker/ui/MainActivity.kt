package com.zzvial.steptracker.ui

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.Navigation
import com.zzvial.steptracker.R
import com.zzvial.steptracker.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)

        appContext = applicationContext
    }

    override fun onDestroy() {
        super.onDestroy()

        coroutineScope.cancel()
    }

    companion object {
        var appContext: Context? = null
        fun getPendingIntent() = PendingIntent.getActivity(
                appContext,
                0,
                Intent(appContext, MainActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT)
    }
}