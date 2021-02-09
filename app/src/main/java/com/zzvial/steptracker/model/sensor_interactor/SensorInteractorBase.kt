package com.zzvial.steptracker.model.sensor_interactor

import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import timber.log.Timber

abstract class SensorInteractorBase(appContext: Context): SensorEventListener {
    var samplingPeriod: Int = SensorManager.SENSOR_DELAY_NORMAL
    var maxReportLatency: Int = 1000000
    open protected val sensorManager by lazy {
        appContext.getSystemService(Activity.SENSOR_SERVICE) as SensorManager
    }
    open abstract protected val sensor: Sensor

    private fun registerListener(listener: SensorEventListener) {
        val res = sensorManager.registerListener(this, sensor, samplingPeriod, maxReportLatency)
        Timber.d("register sensor listener = $res")
    }
    private fun unregisterListener(listener: SensorEventListener) {
        Timber.d("unregister sensor listener")
        sensorManager.unregisterListener(this)
    }

    open fun start() {
        registerListener(this)
    }

    open fun pause() {
        unregisterListener(this)
    }

    open fun stop() {
        unregisterListener(this)
    }
}