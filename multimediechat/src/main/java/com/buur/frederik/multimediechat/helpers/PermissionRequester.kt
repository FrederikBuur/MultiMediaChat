package com.buur.frederik.multimediechat.helpers

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity

object PermissionRequester {

    private const val REQUEST_PERMISSION_CODE = 1000

    fun devicePermissionIsGranted(context: Context): Boolean {
        val writeExternal = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val recordAudio = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)

        return writeExternal == PackageManager.PERMISSION_GRANTED &&
                recordAudio == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermissions(activity: AppCompatActivity?) {
        activity?.let {
            ActivityCompat.requestPermissions(it, arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO)
                    , REQUEST_PERMISSION_CODE)
        }
    }
}