package com.buur.frederik.multimediechatexample.activities

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.inputmethod.InputMethodManager
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

abstract class MMActivity : RxAppCompatActivity() {

    val currentFragment: Fragment?
        get() {
            if (supportFragmentManager.backStackEntryCount == 0) return null

            val name = supportFragmentManager.getBackStackEntryAt(supportFragmentManager.backStackEntryCount - 1).name
            val curr = supportFragmentManager.findFragmentByTag(name)
            return curr
        }

    fun navigateToFragment(fragment: Fragment, argument: Bundle? = null, shouldAddToBackStack: Boolean? = true, shouldAddToContainer: Boolean? = false) {

        //fragment.arguments = argument
        val supFragMan = supportFragmentManager.beginTransaction()

        val fragmentContainer = when (this) {
            is MainActivity -> mainFragmentContainer.id
            else -> {
                null
            }
        }

        if (shouldAddToContainer == true) {
            fragmentContainer?.let { supFragMan.add(fragmentContainer, fragment, fragment.javaClass.toString()) }
        } else {
            fragmentContainer?.let { supFragMan.replace(fragmentContainer, fragment, fragment.javaClass.toString()) }
        }

        if (shouldAddToBackStack == true) {
            supFragMan.addToBackStack(fragment.tag)
        }
        supFragMan.commit()
        hideKeyboard()
    }

    fun hideKeyboard() {

        this.currentFocus?.let {
            val imm = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }

    }

}