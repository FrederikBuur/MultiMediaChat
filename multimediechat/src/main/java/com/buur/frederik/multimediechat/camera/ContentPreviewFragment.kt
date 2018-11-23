package com.buur.frederik.multimediechat.camera


import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.buur.frederik.multimediechat.R
import com.buur.frederik.multimediechat.enums.MMDataType
import com.buur.frederik.multimediechat.helpers.ImageLoader
import com.buur.frederik.multimediechat.models.MMData
import kotlinx.android.synthetic.main.fragment_content_preview.*

class ContentPreviewFragment : Fragment(), View.OnClickListener {

    private var content: String? = null
    private var type: Int? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        arguments?.getString("content")?.let { content = it }
        arguments?.getInt("type")?.let { type = it }
        return inflater.inflate(R.layout.fragment_content_preview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    private fun setupView() {
        type?.let { t ->
            when (t) {
                MMDataType.Video.ordinal -> {
                    setupForVideo()
                }
                MMDataType.Image.ordinal -> {
                    setupForImage()
                }
            }
        }
        confirmContentButton.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v == confirmContentButton) {
            confirmContent()
        }
    }

    private fun confirmContent() {
        content?.let { c ->
            type?.let { t ->
                (context as? CameraActivity)?.let { act ->
                    act.shouldDeleteFile = false
                    act.returnIntentWithResult(c, t)
                }
            }
        }
    }

    private fun setupForImage() {
        contentPreviewVideo.visibility = View.GONE
        contentPreviewImage.visibility = View.VISIBLE

        context?.let { con ->
            content?.let { image ->
                ImageLoader.loadImage(con, image, contentPreviewImage)
            }
        }
    }

    private fun setupForVideo() {
        contentPreviewVideo.visibility = View.VISIBLE
        contentPreviewImage.visibility = View.GONE

        this.content?.let {
            val uri = Uri.parse((it))

            contentPreviewVideo.setVideoURI(uri)
            contentPreviewVideo.start()
            contentPreviewVideo.setOnCompletionListener { mp ->
                mp.start()
            }
        }
    }

    fun handleBackPress() {
        content?.let { c ->
            MMData.deleteFile(c)
        }
        (context as? CameraActivity)?.supportFragmentManager?.popBackStack()
    }

}
