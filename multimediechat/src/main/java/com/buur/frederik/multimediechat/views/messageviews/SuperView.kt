package com.buur.frederik.multimediechat.views.messageviews

import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.buur.frederik.multimediechat.R

abstract class SuperView: FrameLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?)
            : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)

    abstract fun setup(isSender: Boolean, source: String, time: Int? = null)

    fun setParams(isSender: Boolean, view: FrameLayout) {
        val params = view.layoutParams as FrameLayout.LayoutParams

        this.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)

        if (isSender) {
            params.gravity = Gravity.END
            view.background = ContextCompat.getDrawable(context, R.drawable.shape_msg_sender)
        } else {
            params.gravity = Gravity.START
            view.background = ContextCompat.getDrawable(context, R.drawable.shape_msg_receiver)
        }

        view.layoutParams = params
    }

}