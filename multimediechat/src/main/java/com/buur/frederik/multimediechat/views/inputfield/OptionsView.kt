package com.buur.frederik.multimediechat.views.inputfield

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v4.app.Fragment
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.buur.frederik.multimediechat.R
import com.buur.frederik.multimediechat.views.gifpicker.GifPickerActivity
import gun0912.tedbottompicker.TedBottomPicker
import android.provider.MediaStore
import gun0912.tedbottompicker.adapter.GalleryAdapter.PickerTile.GALLERY


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
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE)
        contentSelectionIntent.type = "*/*"
        val intentArray = arrayOf(takePictureIntent, takeVideoIntent)
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent)
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Choose an action")
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray)
        fragment?.startActivityForResult(chooserIntent, MMInputFieldView.CAMERA_REQUEST_CODE)

    }

    fun openGalleryPicker() {
//        first test lib
//        val images = ArrayList<Image>()
//
//        ImagePicker.with(fragment)                         //  Initialize ImagePicker with activity or fragment context
//                .setToolbarColor("#212121")         //  Toolbar color
//                .setStatusBarColor("#000000")       //  StatusBar color (works with SDK >= 21  )
//                .setToolbarTextColor("#FFFFFF")     //  Toolbar text color (Title and Done button)
//                .setToolbarIconColor("#FFFFFF")     //  Toolbar icon color (Back and Camera button)
//                .setProgressBarColor("#4CAF50")     //  ProgressBar color
//                .setBackgroundColor("#212121")      //  Background color
//                .setCameraOnly(false)               //  Camera mode
//                .setMultipleMode(false)              //  Select multiple images or single image
//                .setFolderMode(true)                //  Folder mode
//                .setShowCamera(true)                //  Show camera button
//                .setFolderTitle("Albums")           //  Folder title (works with FolderMode = true)
//                .setImageTitle("Galleries")         //  Image title (works with FolderMode = false)
//                .setDoneTitle("Done")               //  Done button title
//                .setLimitMessage("You have reached selection limit")    // Selection limit message
////                .setMaxSize(10)                     //  Max images can be selected
//                .setSavePath("ImagePicker")         //  Image capture folder name
////                .setSelectedImages(images)          //  Selected images
//                .setAlwaysShowDoneButton(true)      //  Set always show done button in multiple mode
//                .setKeepScreenOn(true)              //  Keep screen on when selecting images
//                .start()                           //  Start ImagePicker

//        second test lib
//        val tedBottomPicker = TedBottomPicker.Builder(context)
//                .setOnImageSelectedListener { uri ->
//                    uri
//                }
//                .showVideoMedia()
//                .showGalleryTile(true)
//                .showCameraTile(true)
//                .create()
//        tedBottomPicker.show(fragment?.fragmentManager)

        val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/* video/*"
        fragment?.startActivityForResult(intent, MMInputFieldView.GALLERY_REQUEST_CODE)
    }

    fun openDokumentPicker() {
    }

    fun openGifPicker(context: Context?, requestCode: Int) {
        val i = Intent(context, GifPickerActivity::class.java)
        fragment?.startActivityForResult(i, requestCode)
    }


}