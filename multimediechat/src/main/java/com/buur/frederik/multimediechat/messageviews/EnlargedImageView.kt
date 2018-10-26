package com.buur.frederik.multimediechat.messageviews

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.MediaController
import com.buur.frederik.multimediechat.R
import com.buur.frederik.multimediechat.enums.MMDataType
import com.buur.frederik.multimediechat.helpers.ImageLoader
import kotlinx.android.synthetic.main.activity_enlarged_image_view.*

class EnlargedImageView : AppCompatActivity() {

    val tag = "EnlargedImageView"

    var source: String? = null
    var type: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //make fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_enlarged_image_view)

        this.source = intent.getStringExtra("source")
        this.type = intent.getIntExtra("type", 0)

        setupView()
    }

    private fun setupView() {
        type?.let { t ->
            when (t) {
                MMDataType.Image.ordinal -> {
                    setupForImage()
                }
                MMDataType.Video.ordinal -> {
                    setupForVideo()
                }
                else -> {
                    Log.e(tag, "unsupported type")
                }
            }
        }

    }

    private fun setupForImage() {
        zoomView.visibility = View.VISIBLE
        videoView.visibility = View.GONE

        this.source?.let { ImageLoader.loadImage(this, it, zoomView) }
    }

    private fun setupForVideo() {
        zoomView.visibility = View.GONE
        videoView.visibility = View.VISIBLE

        this.source?.let {
            val mc = MediaController(this)
            videoView.setMediaController(mc)
            val uri = Uri.parse((it))
            videoView.setVideoURI(uri)
            videoView.start()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(0, 0)
    }

}
