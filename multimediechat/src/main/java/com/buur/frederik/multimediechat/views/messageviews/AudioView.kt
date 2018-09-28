package com.buur.frederik.multimediechat.views.messageviews

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.buur.frederik.multimediechat.R
import com.buur.frederik.multimediechat.models.MMData

class AudioView: SuperView {

    constructor(context: Context) : super(context) {
        View.inflate(context, R.layout.view_audio, this)
    }

    constructor(context: Context, attrs: AttributeSet?)
            : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)

    override fun setup(isSender: Boolean, mmData: MMData, time: Int?) {
    }

}