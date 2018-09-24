package com.buur.frederik.multimediechat.views.inputfield.contentviews

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.FrameLayout
import com.buur.frederik.multimediechat.R
import kotlinx.android.synthetic.main.view_content_audio.view.*
import javax.security.auth.callback.Callback

class ContentAudioView: ContentSuperView {

    init {
        View.inflate(context, R.layout.view_content_audio, this)
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?)
            : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)

    fun setup(keyboardHeight: Int) {
        animateView(keyboardHeight)
    }

    private fun animateView( keyboardHeight: Int) {

        val scaleY = ValueAnimator.ofInt(0, keyboardHeight)
        scaleY.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Int
            val layoutParams = contentAudioContainer.layoutParams
            layoutParams.height = value
            contentAudioContainer.layoutParams = layoutParams
        }

        scaleY.interpolator = FastOutSlowInInterpolator()
        scaleY.duration = 250
        scaleY.start()

    }

    override fun closeAnimation() {

        val scaleY = ValueAnimator.ofInt(contentAudioContainer.measuredHeight, 0)
        scaleY.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Int
            val layoutParams = contentAudioContainer.layoutParams
            layoutParams.height = value
            contentAudioContainer.layoutParams = layoutParams
        }

        scaleY.interpolator = FastOutSlowInInterpolator()
        scaleY.duration = 200


        scaleY.start()

    }

}