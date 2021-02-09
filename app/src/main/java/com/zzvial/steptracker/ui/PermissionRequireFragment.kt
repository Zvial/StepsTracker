package com.zzvial.steptracker.ui

interface PermissionRequireFragment {
    fun onActivityRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    )
}