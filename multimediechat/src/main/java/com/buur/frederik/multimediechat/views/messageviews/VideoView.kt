package com.buur.frederik.multimediechat.views.messageviews

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.View
import android.widget.MediaController
import com.buur.frederik.multimediechat.R
import com.buur.frederik.multimediechat.models.MMData
import kotlinx.android.synthetic.main.view_video.view.*

class VideoView: SuperView {

    var mc: MediaController? = null

    constructor(context: Context) : super(context) {
        View.inflate(context, R.layout.view_video, this)
    }

    constructor(context: Context, attrs: AttributeSet?)
            : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)

    override fun setup(isSender: Boolean, mmData: MMData, time: Int?) {
        this.isSender = isSender
        this.setParams(vidMsgContainer)

        mc = MediaController(context)
        vidMsgContent.setMediaController(mc)
        val uri = Uri.parse((mmData.source))
        vidMsgContent.setVideoURI(uri)

        playVideo()

    }

    private fun playVideo() {
        vidMsgContent.start()
    }

    private fun pauseVideo() {
        vidMsgContent.pause()
    }
}