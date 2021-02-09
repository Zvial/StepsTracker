package com.zzvial.steptracker.model.sensor_interactor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import com.zzvial.steptracker.model.database.DatabaseInteractor
import com.zzvial.steptracker.model.database.data.Step
import com.zzvial.steptracker.servises.SensorObserverService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.timer

class StepDetectorSensorInteractor @Inject constructor(
    @ApplicationContext val appContext: Context
) : SensorInteractorBase(appContext) {

    @Inject lateinit var databaseInteractor: DatabaseInteractor
    private val TIMER_INTERVAL = 10000L
    private val stepsCache = mutableMapOf<Long, Int>() //timestamp, steps
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var pushCacheTimer: Timer? = null

    override val sensor: Sensor by lazy {
        sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR).also {

        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null && event.sensor == sensor) {
            pushSensorDataToCache(event)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun start() {
        super.start()
        SensorObserverService.startSensorService(appContext = appContext, SensorObserverService.getServiceIntent(appContext))
        startCachingTimer()
    }

    override fun pause() {
        super.pause()

        stopCachingTimer()
        //SensorObserverService.stopSensorService(appContext = appContext, SensorObserverService.getServiceIntent(appContext))
    }

    override fun stop() {
        super.stop()

        stopCachingTimer()
        SensorObserverService.stopSensorService(appContext = appContext, SensorObserverService.getServiceIntent(appContext))
    }

    private fun startCachingTimer() {
        pushCacheTimer = timer(period = TIMER_INTERVAL) {
            synchronized(stepsCache) {
                coroutineScope.launch {
                    if (stepsCache.isNotEmpty()) {
                        saveSensorData(stepsCache)
                        stepsCache.clear()
                    }
                }
            }
        }
    }

    private fun stopCachingTimer() {
        pushCacheTimer?.cancel()
    }

    private fun pushSensorDataToCache(event: SensorEvent) {
        synchronized(stepsCache) {
            val timestamp = toNearestWholeMinute(System.currentTimeMillis())
            if (stepsCache.containsKey(timestamp)) {
                stepsCache.replace(timestamp, stepsCache[timestamp]!! + event.values.size)
            } else {
                stepsCache.put(timestamp, event.values.size)
            }
        }
    }

    private suspend fun saveSensorData(steps: Map<Long, Int>) {
        val stepsList = mutableListOf<Step>()
        steps.forEach { timestamp, stepCount ->
            val step = Step(timestamp = timestamp, steps = stepCount)
            stepsList.add(step)
        }

        databaseInteractor.insertSteps(stepsList)

        stepsList.clear()
    }

    private fun toNearestWholeMinute(timestamp: Long): Long {
        val c: Calendar = GregorianCalendar()
        c.time = Date(timestamp)
        if (c[Calendar.SECOND] >= 30) c.add(Calendar.MINUTE, 1)
        c[Calendar.SECOND] = 0
        c[Calendar.MILLISECOND] = 0
        return c.timeInMillis
    }

}
