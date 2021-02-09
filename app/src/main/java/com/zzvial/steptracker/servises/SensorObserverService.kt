package com.zzvial.steptracker.servises

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import com.zzvial.steptracker.R
import com.zzvial.steptracker.model.database.DatabaseInteractor
import com.zzvial.steptracker.helpers.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class SensorObserverService : Service() {
    @Inject lateinit var databaseInteractor: DatabaseInteractor
    var timerIntervalForNotificationUpdate = 10000L

    private val NOTIFICATION_ID = 1090
    private val DEFAULT_TIMER_INTERVAL = 10000L

    private val notifHelper by lazy {
        NotificationHelper(applicationContext, NotificationHelper.channelStepDetectorID, NotificationHelper.channelStepDetectorName)
    }

    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, getDetectorNotification(""))

        timerIntervalForNotificationUpdate = intent?.getLongExtra(EXTRA_TIMER_INTERVAL, DEFAULT_TIMER_INTERVAL) ?: DEFAULT_TIMER_INTERVAL

        var isFirstShown = true
        coroutineScope.launch {
            databaseInteractor.getStepsByToday().collect {
                if(isFirstShown) {
                    updateNotification(it)
                    isFirstShown = false
                }
                delay(timerIntervalForNotificationUpdate)
                updateNotification(it)
            }
        }

        return Service.START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()

        notifHelper.unNotify()
    }

    private fun updateNotification(steps: List<Int>) {
        val stepsCount = steps.lastOrNull()
        val notifText = if(stepsCount==null) {
            "Еще сегодня не ходил"
        } else {
            "Сегодня прошел $stepsCount"
        }
        notifHelper.notify(getDetectorNotification(notifText))
    }

    private fun getDetectorNotification(contentText: String) =
        notifHelper.createServiceNotification(
            NOTIFICATION_ID,
            R.drawable.ic_walking,
            resources.getString(R.string.app_name),
            contentText
        )

    companion object {
        const val EXTRA_TIMER_INTERVAL = "timer_interval"

        fun getServiceIntent(appContext: Context) = Intent(appContext, SensorObserverService::class.java)

        fun startSensorService(appContext: Context, intent: Intent) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                appContext.startForegroundService(intent)
                Timber.d("started foreground service")
            } else {
                appContext.startService(intent)
                Timber.d("started service")
            }
        }

        fun stopSensorService(appContext: Context, intent: Intent) {
            appContext.stopService(intent)
        }
    }

}