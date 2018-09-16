package com.buur.frederik.multimediechat.views

import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.buur.frederik.multimediechat.R
import kotlinx.android.synthetic.main.view_text_message.view.*

open class MessageView: SuperView {

    constructor(context: Context) : super(context) {
        View.inflate(context, R.layout.view_message, this) // TODO fix leak?
    }

    constructor(context: Context, attrs: AttributeSet?)
            : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)

    override fun setup(isSender: Boolean, source: String, time: Int?) {

    }

    fun setParams(isSender: Boolean) {
        val params = LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        if (isSender) {
            params.gravity = Gravity.END
            textMsgContent.background = ContextCompat.getDrawable(context, R.drawable.shape_msg_sender)
        } else {
            params.gravity = Gravity.START
            textMsgContent.background = ContextCompat.getDrawable(context, R.drawable.shape_msg_receiver)
        }
        textMsgContent.layoutParams = params
    }

}