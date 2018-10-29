package com.buur.frederik.multimediechat.helpers

import android.util.Log
import android.view.View
import android.widget.FrameLayout
import com.buur.frederik.multimediechat.models.MMData
import io.reactivex.Observable
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

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