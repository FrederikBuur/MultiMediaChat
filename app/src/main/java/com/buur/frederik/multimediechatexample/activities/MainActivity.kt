package com.buur.frederik.multimediechatexample.activities

import android.os.Bundle
import com.buur.frederik.multimediechatexample.R
import com.buur.frederik.multimediechatexample.fragments.chatfragment.ChatFragment

class MainActivity : MMActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //if (isLoggedin) {
            this.navigateToFragment(ChatFragment())
        //} else {
        //    this.navigateToFragment(LoginFragment())
        //}

    }
}
