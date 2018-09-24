package com.buur.frederik.multimediechat.views.inputfield

import android.app.Activity
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.util.AttributeSet
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import com.buur.frederik.multimediechat.R
import com.buur.frederik.multimediechat.enums.MMDataType
import com.buur.frederik.multimediechat.models.MMData
import com.buur.frederik.multimediechat.views.inputfield.contentviews.ContentAudioView
import com.buur.frederik.multimediechat.views.inputfield.contentviews.ContentSuperView
import com.jakewharton.rxbinding2.view.layoutChangeEvents
import kotlinx.android.synthetic.main.view_mm_input_field.view.*
import kotlinx.android.synthetic.main.view_options.view.*

class MMInputFieldView: FrameLayout, View.OnClickListener {

    private val defaultKeyboardHeight = 873

    private var keyboardHeight: Int = defaultKeyboardHeight
    private var windowMaxSize: Int? = null
    private var activeContentView: ContentSuperView? = null
    private var isKeyboardOpen = false
    private var isOptionsViewSelected: Boolean? = null

    private var activity: AppCompatActivity? = null
    private var rootLayout: View? = null
    private var delegate: ISendMessage? = null

    init {
        View.inflate(context, R.layout.view_mm_input_field, this)
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?)
            : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)

    fun setup(activity: AppCompatActivity, rootLayout: View, deligate: ISendMessage) {
        this.activity = activity
        this.rootLayout = rootLayout
        this.delegate = deligate

        rootLayout.post {
            windowMaxSize = rootLayout.height
            windowResizeListener()
        }

        sendButton.setOnClickListener(this)
        optionsViewGif.setOnClickListener(this)
        optionsViewImage.setOnClickListener(this)
        optionsViewAudio.setOnClickListener(this)
        inputEditText.setOnClickListener(this)
    }

    override fun onClick(v: View?) {

                when (v) {
                    sendButton -> {
                        sendTextMessage()
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

    private fun sendTextMessage() {
        val textMessage = inputEditText.text.toString().trim()

        if (textMessage.isEmpty()) return

        val mmData = MMData(textMessage, MMDataType.Text.ordinal)
        delegate?.sendMMData(mmData)
        inputEditText.text.clear()
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

        rootLayout?.layoutChangeEvents()
                ?.doOnNext {
                    val view = it.view()
                    rootLayout?.post {
                        windowMaxSize?.let { size ->
                            // is keyboard open or not
                            isKeyboardOpen = size > view.height
                            if (view.height > size) {
                                windowMaxSize = view.height // what?
                            }
                            if (isKeyboardOpen) {
                                keyboardHeight = windowMaxSize?.minus(view.height) ?: defaultKeyboardHeight
                            }
                        }
                    }
                }
                ?.subscribe({}, {})
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
}