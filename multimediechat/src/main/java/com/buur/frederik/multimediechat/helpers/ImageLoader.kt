package com.buur.frederik.multimediechat.helpers

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.buur.frederik.multimediechat.R
import kotlin.math.roundToInt

object ImageLoader {

    fun loadImage(context: Context, image: Any, view: ImageView, progress: View? = null) {

        val size = (context.resources.getDimension(R.dimen.view_message_height_max)).roundToInt()

        Glide.with(context)
                .load(image)
                .apply(RequestOptions.centerCropTransform().fitCenter()
                        .placeholder(R.drawable.background_image_placeholder)
                        .error(R.drawable.background_image_placeholder)
                        .override(size, size)
                )
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                        if (view == null) return false
                        view.visibility = View.VISIBLE
                        progress?.visibility = View.INVISIBLE
                        return false
                    }

                    override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                        if (view == null) return false
                        view.visibility = View.VISIBLE
                        progress?.visibility = View.INVISIBLE
                        return false
                    }
                })
                .into(view)
    }

}