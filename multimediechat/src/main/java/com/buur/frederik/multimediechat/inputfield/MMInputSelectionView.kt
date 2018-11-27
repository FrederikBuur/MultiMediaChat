package com.buur.frederik.multimediechat.inputfield

import android.Manifest
import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.buur.frederik.multimediechat.R
import com.buur.frederik.multimediechat.gifpicker.GifPickerActivity
import com.buur.frederik.multimediechat.camera.CameraActivity
import com.buur.frederik.multimediechat.helpers.PermissionRequester
import com.vincent.filepicker.Constant
import com.vincent.filepicker.activity.NormalFilePickActivity


class MMInputSelectionView : FrameLayout {

    private var fragment: Fragment? = null

    init {
        View.inflate(context, R.layout.view_input_selection, this)
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?)
            : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)

    fun setup(fragment: Fragment) {
        this.fragment = fragment
    }

    fun openCamera(requestCode: Int) {
        context?.let { con ->
            if (PermissionRequester.isCameraPermissionGranted(con)) {
                val intent = Intent(context, CameraActivity::class.java)
                fragment?.startActivityForResult(intent, requestCode)
            } else {
                PermissionRequester.requestPermissions(fragment,
                        arrayOf(Manifest.permission.CAMERA,
                                Manifest.permission.RECORD_AUDIO,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        PermissionRequester.CAMERA_REQUEST_PERMISSION_CODE)
            }
        }
    }

    fun openGalleryPicker(requestCode: Int) {
        context?.let { con ->
            if (PermissionRequester.isWriteExternalStorageGranted(con) &&
                    PermissionRequester.isReadExternalStorageGranted(con)) {
                val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                intent.type = "image/* video/*"
                fragment?.startActivityForResult(intent, requestCode)
            } else {
                PermissionRequester.requestPermissions(fragment,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE),
                        PermissionRequester.GALLERY_REQUEST_PERMISSION_CODE)
            }
        }
    }

    fun openDocumentPicker(requestCode: Int) {
        context?.let { con ->
            if (PermissionRequester.isWriteExternalStorageGranted(con) &&
                    PermissionRequester.isReadExternalStorageGranted(con)) {
                val intent = Intent(context, NormalFilePickActivity::class.java)
                intent.putExtra(Constant.MAX_NUMBER, 1)
//        intent.putExtra(NormalFilePickActivity.SUFFIX, arrayOf("xlsx", "xls", "doc", "docx", "ppt", "pptx", "pdf"))
                intent.putExtra(NormalFilePickActivity.SUFFIX, arrayOf("pdf"))
                fragment?.startActivityForResult(intent, requestCode)
            } else {
                PermissionRequester.requestPermissions(fragment,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        PermissionRequester.DOCUMENT_REQUEST_PERMISSION_CODE)
            }
        }

    }

    fun openGifPicker(requestCode: Int) {
        val i = Intent(context, GifPickerActivity::class.java)
        fragment?.startActivityForResult(i, requestCode)
    }


}