package com.buur.frederik.multimediechat.helpers

import android.content.Context
import android.net.Uri
import android.util.Base64
import android.view.View
import android.widget.FrameLayout
import io.reactivex.Observable
import kotlinx.android.synthetic.main.view_audio.view.*
import java.io.*
import java.lang.Exception

object AudioHelper {

    fun currentTimeVisualization(percentDecimal: Float, audioTimeIndicator: FrameLayout, audioIndicatorContainer: View) {
        val params = audioTimeIndicator.layoutParams as FrameLayout.LayoutParams
        val containerWidth = audioIndicatorContainer.width
        params.width = (percentDecimal * containerWidth).toInt()
        audioTimeIndicator.layoutParams = params
    }

}