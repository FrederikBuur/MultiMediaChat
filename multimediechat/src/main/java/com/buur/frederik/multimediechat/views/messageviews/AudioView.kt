package com.buur.frederik.multimediechat.views.messageviews

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Environment
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.buur.frederik.multimediechat.R
import com.buur.frederik.multimediechat.helpers.AudioHelper
import com.buur.frederik.multimediechat.models.MMData
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.view_audio.view.*
import java.lang.Exception
import java.util.concurrent.TimeUnit
import android.widget.FrameLayout
import io.reactivex.disposables.Disposable


class AudioView : SuperView, View.OnClickListener, MediaPlayer.OnCompletionListener {

    private var mmData: MMData? = null
    private var mediaPlayer: MediaPlayer? = null
    private var length: Int? = null

    private var durationDisposable: Disposable? = null

    constructor(context: Context) : super(context) {
        View.inflate(context, R.layout.view_audio, this)
    }

    constructor(context: Context, attrs: AttributeSet?)
            : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)

    override fun setup(isSender: Boolean, mmData: MMData, time: Int?) {
        this.mmData = mmData

        audioCurrentTimeIndicator.alpha = if (isSender) {
            0.3f
        } else {
            0.1f
        }

        setupAudio()

        this.setParams(isSender, audioMsgContainer)
//        this.setTextColor(isSender, audioTimeIndicator)

        audioMsgContainer.setOnClickListener(this)
    }

    private fun setupAudio() {

//        val stringByteArray = this.mmData?.source as? String

//        val audioName = "/MultiMediaAudio_${System.currentTimeMillis()}.3gp"
//        val outputFile = Environment.getExternalStorageDirectory().absolutePath + audioName

//        val disp = AudioHelper.convertStringByteArrayTo3gp(outputFile, stringByteArray)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe({ _ ->
//                    try {
//                        mediaPlayer = MediaPlayer()
//                        mediaPlayer?.setDataSource(outputFile)
//                        mediaPlayer?.isLooping = false
//                        mediaPlayer?.setOnCompletionListener(this)
//                        mediaPlayer?.prepare()
//                        audioTimeIndicator.text = mediaPlayer?.duration?.toFloat()?.div(1000f).toString()
//
//                    } catch (e: Exception) {
//                        e.message
//                        Throwable(e.message)
//                    }
//                }, {
//                    it
//                })
    }

    private fun setupMediaPlayer(audio: String) {

//        val uri = Uri.parse(audio)
        try {
            mediaPlayer = MediaPlayer()

//        if (uri) {
//            mediaPlayer?.setDataSource(context, mmData?.source)
//        } else if (url) {
            mediaPlayer?.setDataSource(audio)
//        }
            mediaPlayer?.isLooping = false
            mediaPlayer?.setOnCompletionListener(this)
            mediaPlayer?.prepare()
//            audioTimeIndicator.text = mediaPlayer?.duration?.toFloat()?.div(1000f).toString()
        } catch (e: Exception) {
            e.message
        }
    }


    private fun setupDurationListener() {

        val max = (mediaPlayer?.duration ?: 1).toFloat()
        durationDisposable = Observable.just(Log.d(this.toString(), "duration listener started"))
                .repeatWhen { completed -> completed.delay(65, TimeUnit.MILLISECONDS) }
                .observeOn(AndroidSchedulers.mainThread())
                .retry()
                .subscribe({ _ ->
                    val current = mediaPlayer?.currentPosition?.toFloat()
                    val percentDecimal = current?.div(max)
                    percentDecimal?.let { currentTimeVisualization(it) }

                }, {
                    val error = it.message
                    error
                })
    }

    private fun currentTimeVisualization(percentDecimal: Float) {
        val params = audioCurrentTimeIndicator.layoutParams as FrameLayout.LayoutParams
        val containerWidth = audioMsgContainer.width
        params.width = (percentDecimal * containerWidth).toInt()
        audioCurrentTimeIndicator.layoutParams = params
    }

    private fun playAudio() {

        if (mediaPlayer?.isPlaying != true) {
            length?.let {
                mediaPlayer?.seekTo(it)
            } ?: kotlin.run {
                (mmData?.source as? String)?.let {
                    setupMediaPlayer(it)
                }
                setupDurationListener()
            }
            mediaPlayer?.start()
            showPauseButton(true)
        } else {
            try {
                mediaPlayer?.pause()
                length = mediaPlayer?.currentPosition
                showPauseButton(false)
            } catch (e: Exception) {
                e.message
            }
        }
    }

    private fun showPauseButton(shouldShowPause: Boolean) {
        val img = if (shouldShowPause) {
            android.R.drawable.ic_media_pause
        } else {
            android.R.drawable.ic_media_play
        }
        Glide.with(this)
                .load(img)
                .into(audioActionImg)
    }

    override fun onCompletion(mp: MediaPlayer?) {
        showPauseButton(false)
        currentTimeVisualization(0f)
        length = null
        if (durationDisposable?.isDisposed == false) {
            durationDisposable?.dispose()
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            audioMsgContainer -> {
                playAudio()
            }
        }
    }

    override fun onDetachedFromWindow() {
        if (durationDisposable?.isDisposed == false) {
            durationDisposable?.dispose()
        }
        super.onDetachedFromWindow()
    }

}