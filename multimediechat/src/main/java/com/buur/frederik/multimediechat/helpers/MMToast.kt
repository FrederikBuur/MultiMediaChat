package com.buur.frederik.multimediechat.helpers

import android.content.Context
import android.widget.Toast

object MMToast {

    private var myToast: Toast? = null

    fun showToast(context: Context?, text: String, duration: Int) {
        myToast?.cancel()
        myToast = Toast.makeText(context?.applicationContext, text, duration)
        myToast?.show()
    }
}