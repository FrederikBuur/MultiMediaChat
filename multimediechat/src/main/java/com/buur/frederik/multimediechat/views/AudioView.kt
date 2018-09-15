package com.buur.frederik.multimediechat.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.buur.frederik.multimediechat.R

class AudioView: SuperView {

    constructor(context: Context) : super(context) {
        View.inflate(context, R.layout.view_audio, this)
    }

    constructor(context: Context, attrs: AttributeSet?)
            : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)

    override fun setup(sender: String, source: String, time: Int?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}