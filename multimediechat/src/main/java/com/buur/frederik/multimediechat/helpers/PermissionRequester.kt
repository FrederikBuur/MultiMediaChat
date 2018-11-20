package com.buur.frederik.multimediechat.helpers

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat

object PermissionRequester {

    const val CAMERA_REQUEST_PERMISSION_CODE = 1000
    const val GALLERY_REQUEST_PERMISSION_CODE = 2000
    const val DOCUMENT_REQUEST_PERMISSION_CODE = 3000

    fun isWriteExternalStorageGranted(context: Context): Boolean {
        val writeExternal = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return writeExternal == PackageManager.PERMISSION_GRANTED
    }

    fun isReadExternalStorageGranted(context: Context): Boolean {
        val readExternal = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
        return readExternal == PackageManager.PERMISSION_GRANTED
    }

    fun isMicrophonePermissionGranted(context: Context): Boolean {
        val recordAudio = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
        return recordAudio == PackageManager.PERMISSION_GRANTED
    }

    fun isCameraPermissionGranted(context: Context): Boolean {
        val useCamera = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
        return useCamera == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermissions(fragment: Fragment?, permissions: Array<String>, permissionRequestCode: Int) {
        fragment?.requestPermissions(permissions
                    , permissionRequestCode)
    }

    fun isPermissionArraySame(arr1: List<String>, arr2: List<String>): Boolean {
        if (arr1.size != arr2.size) return false

        arr1.forEachIndexed { index, s ->
            if (s != arr2[index]) return false
        }

        return true
    }

}