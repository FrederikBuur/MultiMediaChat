package com.buur.frederik.multimediechat.views.inputfield.contentviews

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

abstract class ContentSuperView: FrameLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?)
            : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)

    abstract fun closeAnimation()

}