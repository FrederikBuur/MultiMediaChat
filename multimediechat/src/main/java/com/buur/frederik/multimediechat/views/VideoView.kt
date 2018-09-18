package com.buur.frederik.multimediechat.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.buur.frederik.multimediechat.R

class VideoView: SuperView {

    constructor(context: Context) : super(context) {
        View.inflate(context, R.layout.view_video, this)
    }

    constructor(context: Context, attrs: AttributeSet?)
            : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)

    override fun setup(isSender: Boolean, source: String, time: Int?) {
       //jjbg
    }
}