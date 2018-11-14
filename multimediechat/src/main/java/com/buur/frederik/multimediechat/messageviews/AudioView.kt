package com.buur.frederik.multimediechat.messageviews

import android.content.Context
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.buur.frederik.multimediechat.R
import com.buur.frederik.multimediechat.helpers.AudioHelper
import com.buur.frederik.multimediechat.models.MMData
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.view_audio.view.*
import java.lang.Exception
import java.util.concurrent.TimeUnit
import io.reactivex.disposables.Disposable
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class AudioView : SuperView, View.OnClickListener, MediaPlayer.OnCompletionListener {

    private val tag = "AudioView"

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

    override fun setup(isSender: Boolean, mmData: MMData?, time: Int?) {
        this.isSender = isSender
        this.mmData = mmData

        setupColors(isSender)
//        resetView()

//        val uri = Uri.parse(mmData?.source)
//        val mmr = MediaMetadataRetriever()
//        mmr.setDataSource(context, uri)
//        val duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
//        val millis = Integer.parseInt(duration).toLong()
//
//        val format = "mm:ss"
//        val s = SimpleDateFormat(format, Locale.getDefault()).format(Date(millis))
//        audioLengthTextView.text = s

        this.length = 0
        AudioHelper.currentTimeVisualization(0f, audioCurrentTimeIndicator, audioMsgContainer)

        this.setParams(audioMsgContainer, audioMsgContentContainer)
        this.setupDateAndSender(audioMsgTime, audioMsgSender)
        audioMsgContentContainer.setOnClickListener(this)
    }

    private fun setupColors(isSender: Boolean) {
        if (isSender) {
            audioCurrentTimeIndicator.alpha = 0.3f
        } else {
            audioCurrentTimeIndicator.alpha = 0.1f
        }
        this.setTextColor(audioLengthTextView)
    }

    private fun setupMediaPlayer(audio: String) {
        try {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer()
                mediaPlayer?.reset()
                mediaPlayer?.setDataSource(audio)
                mediaPlayer?.isLooping = false
                mediaPlayer?.setOnCompletionListener(this)
                mediaPlayer?.prepare()
                mediaPlayer?.let {
                    if (it.audioSessionId != -1) {
                        audioVisualizer.setAudioSessionId(it.audioSessionId)
                    }

                }
            }
        } catch (e: Exception) {
            e.message
        }
    }

    private fun setupDurationListener() {

        val max = (mediaPlayer?.duration ?: 1).toFloat()
        durationDisposable = Observable.just(Log.d(tag, "duration listener started"))
                .repeatWhen { completed -> completed.delay(30, TimeUnit.MILLISECONDS) }
                .observeOn(AndroidSchedulers.mainThread())
                .retry()
                .subscribe({ _ ->
                    val current = mediaPlayer?.currentPosition?.toFloat()
                    val percentDecimal = current?.div(max)
                    percentDecimal?.let {
                        AudioHelper.currentTimeVisualization(it, audioCurrentTimeIndicator, audioMsgContainer)
                    }

                }, {
                    it
                })
    }

    override fun onClick(v: View?) {
        when (v) {
            audioMsgContentContainer -> {
                playAudio()
            }
        }
    }

    private fun playAudio() {

        if (mediaPlayer?.isPlaying != true) {
            length?.let {
                mediaPlayer?.seekTo(it)
            } ?: kotlin.run {
                (mmData?.source)?.let {
                    setupMediaPlayer(it)
                    setupDurationListener()
                }
            }
            mediaPlayer?.start()
            audioVisualizer.visibility = View.VISIBLE
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
        resetView()
        resetAudio()
    }

    private fun resetAudio() {
        audioVisualizer.visibility = View.INVISIBLE
        audioVisualizer.release()
        this.mediaPlayer?.let {
            it.stop()
            it.release()
        }
        this.mediaPlayer = null

        if (durationDisposable?.isDisposed == false) {
            durationDisposable?.dispose()
        }
    }

    private fun resetView() {
        showPauseButton(false)
        AudioHelper.currentTimeVisualization(0f, audioCurrentTimeIndicator, audioMsgContainer)
        length = null
    }

    override fun onDetachedFromWindow() {
        resetView()
        resetAudio()
        super.onDetachedFromWindow()
    }

}