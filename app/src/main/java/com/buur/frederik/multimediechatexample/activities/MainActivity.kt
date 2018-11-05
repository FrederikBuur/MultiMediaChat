package com.buur.frederik.multimediechatexample.activities

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.view.View
import com.buur.frederik.multimediechatexample.R
import com.buur.frederik.multimediechatexample.fragments.MMFragment
import com.buur.frederik.multimediechatexample.fragments.chatfragment.ChatFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : MMActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            this.navigateToFragment(ChatFragment(), shouldAddToBackStack = false)
        }

    }

    fun showTopNotification(text: String) {
        notificationText.text = "$text joined"

        val duration = 600L
        val delay = 1500L

        val startAnimation = ObjectAnimator.ofFloat(notificationContainer, View.TRANSLATION_Y, -notificationContainer.measuredHeight.toFloat(), 0f)
        startAnimation.duration = duration
        startAnimation.interpolator = FastOutSlowInInterpolator()

        val endAnimation = ObjectAnimator.ofFloat(notificationContainer, View.TRANSLATION_Y, 1f, -notificationContainer.measuredHeight.toFloat())
        endAnimation.startDelay = delay
        endAnimation.duration = duration
        endAnimation.interpolator = FastOutSlowInInterpolator()

        val animatorSet = AnimatorSet()
        animatorSet.playSequentially(startAnimation, endAnimation)
        animatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator?) {
                notificationContainer.visibility = View.VISIBLE
            }
            override fun onAnimationEnd(p0: Animator?) {
                notificationContainer.visibility = View.INVISIBLE
            }
            override fun onAnimationRepeat(p0: Animator?) {}
            override fun onAnimationCancel(p0: Animator?) {}
        })

        animatorSet.start()

    }

    override fun onBackPressed() {

        val currentFragment = currentFragment as? MMFragment

        if (currentFragment?.handleOnBackPressed() == true) return

        super.onBackPressed()
    }

}
