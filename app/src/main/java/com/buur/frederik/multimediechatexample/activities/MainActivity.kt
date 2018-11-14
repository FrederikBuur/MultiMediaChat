package com.buur.frederik.multimediechatexample.activities

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.view.View
import com.buur.frederik.multimediechatexample.R
import com.buur.frederik.multimediechatexample.controllers.ConnectionHandler
import com.buur.frederik.multimediechatexample.fragments.MMFragment
import com.buur.frederik.multimediechatexample.fragments.chatfragment.ChatFragment
import kotlinx.android.synthetic.main.activity_main.*
import android.support.v4.content.ContextCompat
import com.buur.frederik.multimediechatexample.controllers.MultiMediaApplication
import com.buur.frederik.multimediechatexample.controllers.SessionController
import com.buur.frederik.multimediechatexample.fragments.chatfragment.ChatController
import io.realm.Realm


class MainActivity : MMActivity(), ConnectionHandler.ConnectionReceiverListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            this.navigateToFragment(ChatFragment(), shouldAddToBackStack = false)
        }

    }

    fun showTopNotification(text: String, isPositive: Boolean, openOnlyAnimation: Boolean? = null) {
        notificationText.text = text

        if (isPositive) {
            notificationContainer.setBackgroundColor(ContextCompat.getColor(this, R.color.notification_background_positive))
        } else {
            notificationContainer.setBackgroundColor(ContextCompat.getColor(this, R.color.notification_background_negative))
        }

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

        when (openOnlyAnimation) {
            true -> {
                animatorSet.play(startAnimation)
            }
            false -> {
                endAnimation.startDelay = 0L
                animatorSet.play(endAnimation)
            }
            else -> {
                animatorSet.playSequentially(startAnimation, endAnimation)
            }
        }

        animatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator?) {
                notificationContainer.visibility = View.VISIBLE
            }

            override fun onAnimationEnd(p0: Animator?) {
                if (openOnlyAnimation != true) {
                    notificationContainer.visibility = View.INVISIBLE
                }
            }

            override fun onAnimationRepeat(p0: Animator?) {}
            override fun onAnimationCancel(p0: Animator?) {}
        })

        animatorSet.start()

    }

    override fun onResume() {
        super.onResume()
        ConnectionHandler.setListener(this, this)
    }

    override fun onNetworkChanged(isConnected: Boolean) {
        if (!isConnected) {
            showTopNotification("No Connection", isConnected, openOnlyAnimation = true)
        } else {
            if (notificationContainer.visibility == View.VISIBLE) {
                showTopNotification("No Connection", !isConnected, openOnlyAnimation = false)
            }
//            Realm.getDefaultInstance().use { realm ->
//                val socket = (this.application as? MultiMediaApplication)?.socket
//                ChatController.checkForUnsentMessages(realm, socket)
//            }
        }
    }

    override fun onBackPressed() {

        val currentFragment = currentFragment as? MMFragment

        if (currentFragment?.handleOnBackPressed() == true) return

        super.onBackPressed()
    }

}
