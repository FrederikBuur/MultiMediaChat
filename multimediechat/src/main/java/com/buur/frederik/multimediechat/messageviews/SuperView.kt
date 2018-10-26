package com.buur.frederik.multimediechat.messageviews

import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.TextView
import com.buur.frederik.multimediechat.R
import com.buur.frederik.multimediechat.models.MMData

abstract class SuperView: FrameLayout {

    internal var isSender: Boolean? = null
    internal var mmData: MMData? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?)
            : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)

    abstract fun setup(isSender: Boolean, mmData: MMData, time: Int? = null)

    fun setParams(view: FrameLayout) {
        val params = view.layoutParams as FrameLayout.LayoutParams

        this.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)

        if (this.isSender == true) {
            params.gravity = Gravity.END
            view.background = ContextCompat.getDrawable(context, R.drawable.shape_msg_sender)
        } else {
            params.gravity = Gravity.START
            view.background = ContextCompat.getDrawable(context, R.drawable.shape_msg_receiver)
        }

        val margin = resources.getDimension(R.dimen.view_message_margin).toInt()
        params.setMargins(margin, margin, margin, margin)

        view.layoutParams = params
    }

    fun setTextColor(tv: TextView) {
        val color = if (this.isSender == true) R.color.textWhite else R.color.textDark
        tv.setTextColor(ContextCompat.getColor(context, color))
    }

}