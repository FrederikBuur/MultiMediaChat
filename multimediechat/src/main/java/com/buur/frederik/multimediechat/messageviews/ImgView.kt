package com.buur.frederik.multimediechat.messageviews

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.View
import com.buur.frederik.multimediechat.R
import kotlinx.android.synthetic.main.view_img.view.*
import com.buur.frederik.multimediechat.helpers.ImageLoader
import com.buur.frederik.multimediechat.models.MMData


class ImgView: SuperView, View.OnClickListener {

    constructor(context: Context) : super(context) {
        View.inflate(context, R.layout.view_img, this)

    }

    constructor(context: Context, attrs: AttributeSet?)
            : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)

    override fun setup(isSender: Boolean, mmData: MMData, time: Int?) {
        this.isSender = isSender
        this.mmData = mmData

        this.mmData?.source?.let { ImageLoader.loadImage(context, it, imgMsgContent, imgMsgProgress) }

        this.setParams(imgMsgContainer)
        imgMsgContainer.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v == imgMsgContainer) {
            val intent = Intent(context, EnlargedImageView::class.java)
            mmData?.let { data ->
                intent.putExtra("source", data.source)
                intent.putExtra("type", data.type)
                context.startActivity(intent)
            }
        }
    }

}