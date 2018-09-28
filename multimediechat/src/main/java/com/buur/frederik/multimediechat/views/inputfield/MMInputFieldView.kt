package com.buur.frederik.multimediechat.views.inputfield

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.AttributeSet
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import com.buur.frederik.multimediechat.R
import com.buur.frederik.multimediechat.enums.MMDataType
import com.buur.frederik.multimediechat.models.MMData
import com.buur.frederik.multimediechat.views.MMView
import com.buur.frederik.multimediechat.views.inputfield.contentviews.ContentAudioView
import com.buur.frederik.multimediechat.views.inputfield.contentviews.ContentSuperView
import kotlinx.android.synthetic.main.view_mm_input_field.view.*
import kotlinx.android.synthetic.main.view_options.view.*
import com.buur.frederik.multimediechat.helpers.ImageHelper
import com.nguyenhoanglam.imagepicker.model.Config
import com.nguyenhoanglam.imagepicker.model.Image
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class MMInputFieldView: FrameLayout, View.OnClickListener {

    private val defaultKeyboardHeight = 873

    private var keyboardHeight: Int = defaultKeyboardHeight
    private var windowMaxSize: Int? = null
    private var activeContentView: ContentSuperView? = null
    private var isKeyboardOpen = false
    private var isOptionsViewSelected: Boolean? = null

    private var activity: AppCompatActivity? = null
    private var mmView: View? = null
    private var delegate: ISendMessage? = null
    private var fragment: Fragment? = null

    init {
        View.inflate(context, R.layout.view_mm_input_field, this)
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?)
            : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)

    fun setup(activity: AppCompatActivity, mmView: MMView, delegate: ISendMessage, fragment: Fragment) {
        this.activity = activity
        this.mmView = mmView
        this.delegate = delegate
        this.fragment = fragment

        inputOptionsView.setup(mmView, fragment)

        mmView.post {
            windowMaxSize = mmView.height
            windowResizeListener()
        }

        optionsViewCamera.setOnClickListener(this)
        optionsViewGif.setOnClickListener(this)
        optionsViewAudio.setOnClickListener(this)

        sendButton.setOnClickListener(this)
        inputEditText.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
                when (v) {
                    sendButton -> {
                        sendTextMessage()
                    }
                    optionsViewCamera -> {
                        inputOptionsView.openImagePicker()
                    }
                    optionsViewAudio -> {
                        optionViewAudioOnClick(activity)
                    }
                    inputEditText -> {
                        inputEditTextOnClick(activity)
                    }
                    else -> {
                    }
                }
    }


    // takes data, converts into MMData, calls send
    fun convertToMMData(data: Any?, type: MMDataType) {

        when(type) {

            MMDataType.Text -> {
                val message = (data as? String) ?: "Something went wrong"
                sendMMDataToCaller(MMData(message, type.ordinal))
            }

            MMDataType.Image -> {
                val image = (data as Intent).getParcelableArrayListExtra<Image>(Config.EXTRA_IMAGES).first().path
                ImageHelper.convertUriStringToBitmapString(image, context)
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            sendMMDataToCaller(MMData(it, type.ordinal))
                        }, {})
            }
            MMDataType.Video -> {} //TODO()
            MMDataType.Audio -> {} //TODO()
            MMDataType.Gif -> {} //TODO()
        }
    }

    // returns MMData to caller
    private fun sendMMDataToCaller(mmData: MMData) {
        delegate?.sendMMData(mmData)
    }

    // takes text from edit text and sends to mmData converter
    private fun sendTextMessage() {
        val textMessage = inputEditText.text.toString().trim()

        if (textMessage.isEmpty()) return

        inputEditText.text.clear()
        convertToMMData(textMessage, MMDataType.Text)
    }

    private fun inputEditTextOnClick(activity: AppCompatActivity?) {
        isOptionsViewSelected = false

        when(activeContentView) {
            is ContentAudioView -> activeContentView?.closeAnimation()
        }

        activeContentView = null
        hideKeyboard(activity, false)
    }



    private fun optionViewAudioOnClick(activity: AppCompatActivity?) {
        if (activeContentView is ContentAudioView) return

        isOptionsViewSelected = true
        hideKeyboard(activity, true)

        val audioView = ContentAudioView(context)
        audioView.setup(keyboardHeight)
        activeContentView = audioView

        inputMediaContentView.removeAllViews()
        inputMediaContentView.addView(audioView)

        inputMediaContentView.visibility = View.VISIBLE
    }

    private fun windowResizeListener() {

//        val entry = activity?.supportFragmentManager?.getBackStackEntryAt(activity?.supportFragmentManager?.backStackEntryCount?.minus(1) ?: return)?.name
//        val currentFrag = activity?.supportFragmentManager?.findFragmentByTag(entry) ?: return

//        mmView?.layoutChangeEvents()
//                ?.doOnNext {
//                    val view = it.view()
//                    mmView?.post {
//                        windowMaxSize?.let { size ->
//                            // is keyboard open or not
//                            isKeyboardOpen = size > view.height
//                            if (view.height > size) {
//                                windowMaxSize = view.height // what?
//                            }
//                            if (isKeyboardOpen) {
//                                keyboardHeight = windowMaxSize?.minus(view.height) ?: defaultKeyboardHeight
//                            }
//                        }
//                    }
//                }
//                ?.subscribe({}, {})
    }

    fun hideContentViews() {
        hideKeyboard(activity, true)

    }

    fun getEditText(): EditText {
        return inputEditText
    }

    private fun hideKeyboard(activity: AppCompatActivity?, shouldHide: Boolean) {

        activity?.currentFocus?.let {
            val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            if (shouldHide) {
                imm.hideSoftInputFromWindow(it.windowToken, 0)
            } else {
                imm.showSoftInput(it, 0)
            }
        }

    }

    companion object {

        const val GALLERY_REQUEST_CODE = Config.RC_PICK_IMAGES

    }

}