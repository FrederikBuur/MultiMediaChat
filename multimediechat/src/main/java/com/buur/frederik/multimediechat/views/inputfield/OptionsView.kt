package com.buur.frederik.multimediechat.views.inputfield

import android.content.Context
import android.content.Intent
import android.support.v4.app.Fragment
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.buur.frederik.multimediechat.R
import com.buur.frederik.multimediechat.views.gifpicker.GifPickerActivity
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import com.vincent.filepicker.Constant.REQUEST_CODE_PICK_FILE
import android.support.v4.app.ActivityCompat.startActivityForResult
import com.vincent.filepicker.Constant
import com.vincent.filepicker.activity.NormalFilePickActivity
import com.vincent.filepicker.Constant.MAX_NUMBER




class OptionsView : FrameLayout {

    private var fragment: Fragment? = null
    private var optionsContainer: FrameLayout? = null
    private var rootLayout: View? = null

    init {
        View.inflate(context, R.layout.view_options, this)
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?)
            : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)

    fun setup(rootLayout: View, fragment: Fragment) {
        this.fragment = fragment
        this.rootLayout = rootLayout
    }

    fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val takeVideoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)

        val chooserIntent = Intent(Intent.ACTION_CHOOSER)
        val contentSelectionIntent = Intent(Intent.ACTION_GET_CONTENT)
//        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE)
//        contentSelectionIntent.type = "*/*"
        val intentArray = arrayOf(takePictureIntent, takeVideoIntent)
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent)
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Choose an action")
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray)
        fragment?.startActivityForResult(chooserIntent, MMInputFieldView.CAMERA_REQUEST_CODE)

    }

    fun openGalleryPicker() {
        val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/* video/*"
        fragment?.startActivityForResult(intent, MMInputFieldView.GALLERY_REQUEST_CODE)
    }

    fun openDocumentPicker() {
//        var intent = Intent(Intent.ACTION_GET_CONTENT)
//        intent.type = "application/pdf"
//        intent = Intent.createChooser(intent, "Choose a file")
//        fragment?.startActivityForResult(intent, MMInputFieldView.DOCUMENT_REQUEST_CODE)

        val intent = Intent(context, NormalFilePickActivity::class.java)
        intent.putExtra(Constant.MAX_NUMBER, 1)
//        intent.putExtra(NormalFilePickActivity.SUFFIX, arrayOf("xlsx", "xls", "doc", "docx", "ppt", "pptx", "pdf"))
        intent.putExtra(NormalFilePickActivity.SUFFIX, arrayOf("pdf"))
        fragment?.startActivityForResult(intent, MMInputFieldView.DOCUMENT_REQUEST_CODE)

    }

    fun openGifPicker(context: Context?, requestCode: Int) {
        val i = Intent(context, GifPickerActivity::class.java)
        fragment?.startActivityForResult(i, requestCode)
    }


}