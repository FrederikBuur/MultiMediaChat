package com.buur.frederik.multimediechat.helpers

import android.view.View
import android.widget.FrameLayout
import com.buur.frederik.multimediechat.models.MMData

object AudioHelper {

    var currentAudioFile: MMData? = null
    var currentAudioTime: Float? = null

    fun currentTimeVisualization(percentDecimal: Float, audioTimeIndicator: FrameLayout, audioIndicatorContainer: View) {
        val params = audioTimeIndicator.layoutParams as FrameLayout.LayoutParams
        val containerWidth = audioIndicatorContainer.width
        params.width = (percentDecimal * containerWidth).toInt()
        audioTimeIndicator.layoutParams = params
    }

}