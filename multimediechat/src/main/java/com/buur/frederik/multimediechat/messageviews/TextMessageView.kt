package com.buur.frederik.multimediechat.messageviews

import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import com.buur.frederik.multimediechat.R
import com.buur.frederik.multimediechat.models.MMData
import kotlinx.android.synthetic.main.view_text_message.view.*

class TextMessageView: SuperView {

    constructor(context: Context) : super(context) {
        View.inflate(context, R.layout.view_text_message, this)
    }

    constructor(context: Context, attrs: AttributeSet?)
            : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)

    override fun setup(isSender: Boolean, mmData: MMData, time: Int?) {
        this.isSender = isSender
        textMsgContent.text = (mmData.source as? String) ?: "Error"

        val color = if (isSender) R.color.textWhite else R.color.textDark
        textMsgContent.setTextColor(ContextCompat.getColor(context, color))

        this.setParams(textMsgContainer)

    }

}