package com.buur.frederik.multimediechat.gifpicker

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.buur.frederik.multimediechat.R
import com.buur.frederik.multimediechat.models.gif.GifData
import kotlinx.android.synthetic.main.view_gif.view.*

class GifView : FrameLayout, View.OnClickListener {

    private var gifClickDelegate: IGifOnClick? = null
    private var gifData: GifData? = null

    constructor(context: Context) : super(context) {
        View.inflate(context, R.layout.view_gif, this)
    }

    constructor(context: Context, attrs: AttributeSet?)
            : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)

    fun setup(gifData: GifData?, gifClickDelegate: IGifOnClick) {
        this.gifData = gifData
        this.gifClickDelegate = gifClickDelegate

        insertGifIntoImageView(gifData?.images?.preview_gif?.url)
        gifImageContainer.setOnClickListener(this)
    }

    private fun insertGifIntoImageView(url: String?) {
        Glide.with(context)
                .asGif()
                .load(url)
                .apply(RequestOptions.centerCropTransform().centerCrop()
                        .placeholder(R.drawable.backgroung_gif_placeholder)
                        .override(200, 200)
                )
                .listener(object : RequestListener<GifDrawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<GifDrawable>?, isFirstResource: Boolean): Boolean {
                        return false
                    }

                    override fun onResourceReady(resource: GifDrawable?, model: Any?, target: Target<GifDrawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        return false
                    }
                })
                .into(gifImageView)
    }

    override fun onClick(v: View?) {
        if (v == gifImageContainer) {
            gifData?.images?.original?.url?.let { gifUrl ->
                gifClickDelegate?.GifOnclick(gifUrl)
            }
        }
    }

}