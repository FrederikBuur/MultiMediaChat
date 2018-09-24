package com.buur.frederik.multimediechat.views.inputfield

import android.app.Activity
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.buur.frederik.multimediechat.R
import com.buur.frederik.multimediechat.views.inputfield.contentviews.ContentAudioView
import com.buur.frederik.multimediechat.views.inputfield.contentviews.ContentSuperView
import kotlinx.android.synthetic.main.view_mm_input_field.view.*
import kotlinx.android.synthetic.main.view_options.view.*
import javax.security.auth.callback.Callback

class MMInputFieldView: FrameLayout, View.OnClickListener {

    private val defaultKeyboardHeight = 873

    private var keyboardHeight: Int = defaultKeyboardHeight
    private var windowMaxSize: Int? = null

    private var activeContentView: ContentSuperView? = null

    private var activity: AppCompatActivity? = null
    private var rootLayout: View? = null
    private var isKeyboardOpen = false
    private var isOptionsViewSelected: Boolean? = null

    init {
        View.inflate(context, R.layout.view_mm_input_field, this)
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?)
            : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)

    fun setup(activity: AppCompatActivity, rootLayout: View) {
        this.activity = activity
        this.rootLayout = rootLayout

        rootLayout.post {
            windowMaxSize = rootLayout.height
            windowResizeListener()
        }

//        inputEditText.setOnTouchListener { view, motionEvent ->
//            val action = motionEvent.action
//            Log.d("touchaa", "action: $action")
//            if (action == MotionEvent.ACTION_UP) {
//                inputEditTextOnClick(activity)
//                inputEditText.requestFocus()
//                true // dont do anything
//            } else if (action == MotionEvent.ACTION_DOWN ||
//                    action == MotionEvent.ACTION_MOVE){
//                true // not handled
//            } else {
//                false
//            }
//        }

        optionsViewGif.setOnClickListener(this)
        optionsViewGallery.setOnClickListener(this)
        optionsViewAudio.setOnClickListener(this)
        inputEditText.setOnClickListener(this)
    }

    override fun onClick(v: View?) {

                when (v) {
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

    private fun inputEditTextOnClick(activity: AppCompatActivity?) {
        isOptionsViewSelected = false

        when(activeContentView) {
            is ContentAudioView -> activeContentView?.closeAnimation()
        }

        activeContentView = null
        hideKeyboard(activity, false)
        //inputMediaContentView.visibility = View.GONE
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
        rootLayout?.addOnLayoutChangeListener { view, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->

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
    }

    fun hideContentViews() {
        hideKeyboard(activity, true)

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