package com.zzvial.steptracker.ui.main_screen

import android.Manifest
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModelProvider
import com.zzvial.steptracker.databinding.FragmentMainScreenBinding
import com.zzvial.steptracker.helpers.PermissionHelper
import com.zzvial.steptracker.ui.PermissionRequireFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class MainScreenFragment : Fragment() {
    private lateinit var binding: FragmentMainScreenBinding
    private val viewModel by lazy {
        ViewModelProvider(this).get(MainScreenViewModel::class.java)
    }
    private lateinit var permisHelper: PermissionHelper
    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())
    private val activityPermission = Manifest.permission.ACTIVITY_RECOGNITION

    override fun onAttach(context: Context) {
        super.onAttach(context)

        permisHelper = PermissionHelper(this, permission = activityPermission, coroutineScope = coroutineScope)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainScreenBinding.inflate(inflater, container, false)

        viewModel.stepsCount.observe(viewLifecycleOwner) {
            binding.tvStepsCount.setText(it.toString())
        }

        binding.btnStart.setOnClickListener {
            permisHelper.grantePermission()
            coroutineScope.launch {
                permisHelper.permissionGranted.collect {
                    when (it) {
                        null -> {
                        }
                        true -> viewModel.onStartCounting()
                        false -> viewModel.onStopCounting()
                    }
                }
            }
        }

        binding.btnPause.setOnClickListener {
            viewModel.onPauseCounting()
        }

        binding.btnStop.setOnClickListener {
            viewModel.onStopCounting()
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        viewModel.onStart()
    }

    override fun onStop() {
        super.onStop()

        viewModel.onStop()
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            MainScreenFragment().apply {

            }
    }
}