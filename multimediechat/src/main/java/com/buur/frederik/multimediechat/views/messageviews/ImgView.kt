package com.buur.frederik.multimediechat.views.messageviews

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.View
import com.bumptech.glide.Glide
import com.buur.frederik.multimediechat.R
import kotlinx.android.synthetic.main.view_img.view.*
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.buur.frederik.multimediechat.helpers.ImageHelper
import com.buur.frederik.multimediechat.helpers.ImageLoader
import com.buur.frederik.multimediechat.models.MMData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class ImgView: SuperView {

    constructor(context: Context) : super(context) {
        View.inflate(context, R.layout.view_img, this)

    }

    constructor(context: Context, attrs: AttributeSet?)
            : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)

    override fun setup(isSender: Boolean, mmData: MMData, time: Int?) {
        this.isSender = isSender
        val image = mmData.source

        ImageLoader.loadImage(context, image, imgMsgContent, imgMsgProgress)

        this.setParams(imgMsgContainer)

    }
}