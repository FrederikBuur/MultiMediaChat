package com.buur.frederik.multimediechatexample.fragments.loginfragment

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.buur.frederik.multimediechatexample.R
import com.buur.frederik.multimediechatexample.controllers.MultiMediaApplication
import com.buur.frederik.multimediechatexample.fragments.MMFragment
import com.buur.frederik.multimediechatexample.fragments.chatfragment.ChatController
import com.buur.frederik.multimediechatexample.models.User
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : MMFragment(), View.OnClickListener {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    private fun setupViews() {
        animateLoginPage()
        loginPrimaryAction.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v == loginPrimaryAction) {
            loginClick()
        }
    }

    private fun loginClick() {
        val userName = loginNameEditText.text.toString()
        if (userName.isNotEmpty()) {
            Realm.getDefaultInstance().use { realm ->
                User.createUser(realm, userName)
                (context as? AppCompatActivity)?.let {
                    val socket = (it.application as? MultiMediaApplication)?.socket
                    ChatController.emitUserIsConnected(socket)
                }
                animateLoginPage(false)
            }
        } else {
            loginTextInputLayout.error = "You need to enter your name"
        }
    }

    private fun animateLoginPage(openAnimation: Boolean = true) {

        val containerAlphaAnimation = ObjectAnimator.ofFloat(loginContainer, View.ALPHA,
                if (openAnimation) 0f else 1f,
                if (openAnimation) 1f else 0f)

        containerAlphaAnimation.duration = 500

        containerAlphaAnimation.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                if (!openAnimation) {
                    try {
                        mainActivity?.supportFragmentManager?.popBackStack()
                    } catch (e: IllegalStateException) {
                        e.message
                    }
                }
            }
        })
        containerAlphaAnimation.start()

    }

    override fun handleOnBackPressed(): Boolean {
        mainActivity?.finish()
        return true
    }

}