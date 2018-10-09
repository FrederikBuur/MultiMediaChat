package com.buur.frederik.multimediechatexample.activities

import android.os.Bundle
import android.util.Log
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

    override fun onBackPressed() {

        val currentFragment = currentFragment as? MMFragment

        if (currentFragment?.handleOnBackPressed() == true) return

        super.onBackPressed()
    }

}
