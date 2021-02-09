package com.zzvial.steptracker.ui.main_screen

import android.Manifest
import androidx.annotation.RequiresPermission
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zzvial.steptracker.model.database.DatabaseInteractor
import com.zzvial.steptracker.model.sensor_interactor.StepDetectorSensorInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor() : ViewModel() {

    @Inject lateinit var databaseInteractor: DatabaseInteractor
    @Inject lateinit var stepDetectorSensorInteractor: StepDetectorSensorInteractor

    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())

    //live data
    private val _stepsCount = MutableLiveData<Int>()
    val stepsCount: LiveData<Int>
        get() = _stepsCount

    fun onStart() {
        startCollectStepsCount()
    }

    fun onStop() {
        coroutineScope.cancel()
    }

    @RequiresPermission(Manifest.permission.ACTIVITY_RECOGNITION)
    fun onStartCounting() {
        stepDetectorSensorInteractor.start()
    }

    fun onPauseCounting() {
        stepDetectorSensorInteractor.pause()
    }

    fun onStopCounting() {
        stepDetectorSensorInteractor.stop()
    }

    override fun onCleared() {
        super.onCleared()

        coroutineScope.cancel()
    }

    private fun startCollectStepsCount() {
        coroutineScope.launch {
            databaseInteractor.getStepsByToday().collect {
                _stepsCount.value = it.firstOrNull() ?: 0
            }
        }
    }

}