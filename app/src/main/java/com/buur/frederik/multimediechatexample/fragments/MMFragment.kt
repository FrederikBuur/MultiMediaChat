package com.buur.frederik.multimediechatexample.fragments

import com.buur.frederik.multimediechatexample.activities.MainActivity
import com.trello.rxlifecycle3.components.support.RxFragment

abstract class MMFragment: RxFragment() {

    protected val mainActivity: MainActivity?
        get() {
            val activity = activity
            return if (activity is MainActivity) {
                getActivity() as MainActivity?
            } else {
                null
            }
        }

    open fun handleOnBackPressed(): Boolean {
        return false
    }

}