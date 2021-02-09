package com.zzvial.steptracker.helpers

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.zzvial.steptracker.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class PermissionHelper(
    private val fragment: Fragment,
    private val permission: String,
    private val coroutineScope: CoroutineScope? = null,
    private val handler: (() -> Unit)? = null
) {
    private var isRationaleShown = false
    private val context = fragment.context
    val permissionGranted: MutableStateFlow<Boolean?> = MutableStateFlow(null)

    fun grantePermission() {
        fragment.activity?.let {
            when {
                permissionIsGranted() -> onPermissionGranted()
                it.shouldShowRequestPermissionRationale(permission) ->
                    showPermissionExplanationDialog()
                isRationaleShown -> showPermissionDeniedDialog()
                else -> requestPermission()
            }
        }
    }

    fun permissionIsGranted() = checkPermission()==PackageManager.PERMISSION_GRANTED

    fun permissionIsGranted(requestCode: Int,
                            permissions: Array<out String>,
                            grantResults: IntArray,
                            permis: String): Boolean {
        if(requestCode == PERMISSION_REQUEST_CODE) {
            val index = permissions.indexOf(permis)
            return grantResults.getOrNull(index) == PackageManager.PERMISSION_GRANTED
        }

        return false
    }

    private fun checkPermission() = fragment.activity?.let { ContextCompat.checkSelfPermission(it, permission) }

    //@RequiresPermission(Manifest.permission.ACTIVITY_RECOGNITION)
    private fun onPermissionGranted() {
        context?.let {
            handler?.invoke()
        }

        coroutineScope?.launch {
            permissionGranted.emit(true)
        }
    }

    private fun onPermissionNotGranted() {
        context?.let { context ->
            Toast.makeText(context, R.string.permission_non_granted_text, Toast.LENGTH_SHORT).show()
        }

        coroutineScope?.launch {
            permissionGranted.emit(false)
        }
    }

    private fun requestPermission() {
        //fragment.activity?.requestPermissions(arrayOf(permission), PERMISSION_REQUEST_CODE)

        val requestPermissionLauncher = fragment.requireActivity().registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                onPermissionGranted()

            } else {
                onPermissionNotGranted()
            }
        }
        requestPermissionLauncher.launch(permission)

    }

    private fun showPermissionExplanationDialog() {
        context?.let {
            AlertDialog.Builder(it)
                .setMessage(R.string.permission_dialog_explanation_text)
                .setPositiveButton(R.string.dialog_positive_button) { dialog, _ ->
                    isRationaleShown = true
                    requestPermission()
                    dialog.dismiss()
                }
                .setNegativeButton(R.string.dialog_negative_button) { dialog, _ ->
                    onPermissionNotGranted()
                    dialog.dismiss()
                }
                .show()
        }
    }

    private fun showPermissionDeniedDialog() {
        context?.let {
            AlertDialog.Builder(it)
                .setMessage(R.string.permission_dialog_denied_text)
                .setPositiveButton(R.string.dialog_positive_button) { dialog, _ ->
                    it.startActivity(
                        Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.parse("package:" + it.packageName)
                        )
                    )
                    dialog.dismiss()
                }
                .setNegativeButton(R.string.dialog_negative_button) { dialog, _ ->
                    onPermissionNotGranted()
                    dialog.dismiss()
                }
                .show()
        }
    }

    companion object {
        const val PERMISSION_REQUEST_CODE = 1000
    }
}