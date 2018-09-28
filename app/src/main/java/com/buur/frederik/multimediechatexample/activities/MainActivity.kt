package com.buur.frederik.multimediechatexample.activities

import android.os.Bundle
import com.buur.frederik.multimediechatexample.R
import com.buur.frederik.multimediechatexample.fragments.chatfragment.ChatFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : MMActivity() {

    var height: Int? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //if (isLoggedIn) {
            this.navigateToFragment(ChatFragment(), shouldAddToBackStack = false)
        //} else {
        //    this.navigateToFragment(LoginFragment(), shouldAddToBackStack = false)
        //}


    }
}
