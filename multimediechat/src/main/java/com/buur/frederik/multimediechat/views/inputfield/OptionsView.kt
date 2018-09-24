package com.buur.frederik.multimediechat.views.inputfield

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.buur.frederik.multimediechat.R

class OptionsView: FrameLayout {

    var activity: AppCompatActivity? = null
    var optionsContainer: FrameLayout? = null
    var rootLayout: View? = null

    init {
        View.inflate(context, R.layout.view_options, this)
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?)
            : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)

    fun setup(optionsContainer: FrameLayout, rootLayout: View, activity: AppCompatActivity) {
        this.activity = activity
        this.optionsContainer = optionsContainer
        this.rootLayout = rootLayout
    }


}