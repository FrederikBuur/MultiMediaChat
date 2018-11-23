package com.buur.frederik.multimediechat.messageviews

import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.buur.frederik.multimediechat.R
import com.buur.frederik.multimediechat.models.MMData
import java.text.SimpleDateFormat
import java.util.*

abstract class SuperView : FrameLayout {

    internal var isSender: Boolean? = null
    internal var mmData: MMData? = null
    internal var previousMMData: MMData? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?)
            : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)

    abstract fun setup(isSender: Boolean, mmData: MMData?, previousMMData: MMData?)

    fun setParams(viewContainer: FrameLayout, viewWithBackground: View? = null, viewExtra: View? = null) {
        val params1 = viewContainer.layoutParams as FrameLayout.LayoutParams
        val params2 = viewWithBackground?.layoutParams as? LinearLayout.LayoutParams

        this.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)

        if (this.isSender == true) {
            params1.gravity = Gravity.END
            params2?.gravity = Gravity.END
            viewWithBackground?.background = ContextCompat.getDrawable(context, R.drawable.shape_msg_sender)

        } else {
            params1.gravity = Gravity.START
            params2?.gravity = Gravity.START
            viewWithBackground?.background = ContextCompat.getDrawable(context, R.drawable.shape_msg_receiver)
        }

        val margin = resources.getDimension(R.dimen.view_message_margin).toInt()
        params1.setMargins(margin,
                if (isSameSender()) {
                    0
                } else {
                    margin
                },
                margin,
                margin)

        viewContainer.layoutParams = params1
        viewWithBackground?.layoutParams = params2
        viewExtra?.layoutParams = params2
    }

    private fun isSameSender(): Boolean {
        return previousMMData?.sender_id == this.mmData?.sender_id
    }

    fun setTextColor(tv: TextView) {
        val color = if (this.isSender == true) R.color.textBright else R.color.textDark
        tv.setTextColor(ContextCompat.getColor(context, color))
    }

    fun setupDateAndSender(dateView: TextView, senderView: TextView, linearLayout: LinearLayout) {

        val name = mmData?.sender_name
        val date = mmData?.date

        if (name == null && date == null) {
            dateView.visibility = View.GONE
            senderView.visibility = View.GONE
            return
        }

        val lps = senderView.layoutParams as LinearLayout.LayoutParams

        dateView.visibility = date?.let {
            dateView.text = getDateFormat(it)
            View.VISIBLE
        } ?: kotlin.run {
            View.GONE
        }

        if (this.isSender == true) {
            linearLayout.removeView(dateView)
            linearLayout.addView(dateView, 0)

            senderView.visibility = View.GONE
        } else {
            linearLayout.removeView(dateView)
            linearLayout.addView(dateView, 1)

            lps.gravity = Gravity.START
            name?.let {
                senderView.text = it
                senderView.visibility = View.VISIBLE
            }
        }

        if (isSameSender()) {
            senderView.visibility = View.GONE
        }
        senderView.layoutParams = lps
    }

    private fun getDateFormat(date: Long): String? {
        val currentTime = System.currentTimeMillis()

        val sendDay = SimpleDateFormat("dd", Locale.getDefault()).format(Date(date)).toIntOrNull()
                ?: 0
        val sendYear = SimpleDateFormat("yy", Locale.getDefault()).format(Date(date)).toIntOrNull()
                ?: 0
        val currentDay = SimpleDateFormat("dd", Locale.getDefault()).format(Date(currentTime)).toIntOrNull()
                ?: 0
        val currentYear = SimpleDateFormat("yy", Locale.getDefault()).format(Date(currentTime)).toIntOrNull()
                ?: 0

        val format = if (sendDay == currentDay && sendYear == currentYear &&
                sendDay != 0 && currentDay != 0 && sendYear != 0 && currentYear != 0) {
            // send today get clock time
            "HH:mm"
        } else if (sendDay != 0 && currentDay != 0 && sendYear != 0 && currentYear != 0) {
            // send more than a day ago show date with year
            "dd:MMM"
        } else {
            null
        }
        return format?.let { f ->
            SimpleDateFormat(f, Locale.getDefault()).format(Date(date))
        }
    }

}