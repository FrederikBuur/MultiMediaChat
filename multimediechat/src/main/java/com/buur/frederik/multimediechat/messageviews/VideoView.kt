package com.buur.frederik.multimediechat.messageviews

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.AttributeSet
import android.view.View
import com.buur.frederik.multimediechat.R
import com.buur.frederik.multimediechat.helpers.ImageLoader
import com.buur.frederik.multimediechat.models.MMData
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.view_video.view.*

class VideoView: SuperView, View.OnClickListener {

    private var disp: Disposable? = null

    constructor(context: Context) : super(context) {
        View.inflate(context, R.layout.view_video, this)
    }

    constructor(context: Context, attrs: AttributeSet?)
            : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)

    override fun setup(isSender: Boolean, mmData: MMData?, previousMMData: MMData?) {
        this.isSender = isSender
        this.mmData = mmData
        this.previousMMData = previousMMData

        this.mmData?.source?.let { ImageLoader.loadImage(context, it, vidMsgContent, progress = imgMsgProgress, asBitmap = true) }

        this.setParams(vidMsgContainer)
        this.setupDateAndSender(vidMsgTime, vidMsgSender, vidMsgLL)
        vidMsgContentContainer.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v == vidMsgContentContainer) {
            val intent = Intent(context, EnlargedContentView::class.java)
            this.mmData?.let { data ->
                intent.putExtra("source", data.source)
                intent.putExtra("type", data.type)
                context.startActivity(intent)
            }
        }
    }

    override fun onDetachedFromWindow() {
        if (disp?.isDisposed == false) {
            disp?.dispose()
        }
        super.onDetachedFromWindow()
    }

}