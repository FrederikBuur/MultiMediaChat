package com.buur.frederik.multimediechatexample.activities

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.inputmethod.InputMethodManager
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

abstract class MMActivity: RxAppCompatActivity() {

    fun navigateToFragment(fragment: Fragment, argument: Bundle? = null, shouldAddToBackStack: Boolean? = true) {

        fragment.arguments = argument
        val supFragMan = supportFragmentManager.beginTransaction()

        val fragmentContainer = when (this) {
            is MainActivity -> mainFragmentContainer.id
            else -> {
                null
            }
        }

        fragmentContainer?.let { supFragMan.replace(fragmentContainer, fragment) }

        if (shouldAddToBackStack == true) {
            supFragMan.addToBackStack(fragment.tag)
        }
        supFragMan.commit()
        hideKeyboard()
    }

    private fun hideKeyboard() {

        this.currentFocus?.let {
            val imm = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }

    }

}