package com.buur.frederik.multimediechat.views.inputfield

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.buur.frederik.multimediechat.R

class MMInputFieldView: FrameLayout {

    init {
        View.inflate(context, R.layout.view_mm_input_field, this)
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?)
            : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)

}