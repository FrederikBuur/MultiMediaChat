package com.buur.frederik.multimediechat.messageviews

import android.content.Context
import androidx.core.content.ContextCompat
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.buur.frederik.multimediechat.R
import com.buur.frederik.multimediechat.helpers.AudioHelper
import com.buur.frederik.multimediechat.models.MMData
import com.github.satoshun.reactivex.exoplayer2.PlayerStateChangedEvent
import com.google.android.exoplayer2.Player
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.view_audio.view.*
import java.util.concurrent.TimeUnit
import io.reactivex.disposables.Disposable
import java.text.SimpleDateFormat
import java.util.*


class AudioView : SuperView, View.OnClickListener {

    private val tag = "AudioView"

    private var durationDisposable: Disposable? = null

    constructor(context: Context) : super(context) {
        View.inflate(context, R.layout.view_audio, this)
    }

    constructor(context: Context, attrs: AttributeSet?)
            : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)

    override fun setup(isSender: Boolean, mmData: MMData?, previousMMData: MMData?) {
        this.isSender = isSender
        this.mmData = mmData
        this.previousMMData = previousMMData

        setupColors(isSender)

        if (AudioHelper.currentMMData?.id != this.mmData?.id) {
            AudioHelper.currentTimeVisualization(0f, audioCurrentTimeIndicator, audioMsgContainer)
        }

        this.mmData?.size?.let { size ->
            val format = "mm:ss"
            val time = SimpleDateFormat(format,  Locale.getDefault()).format(Date(size))
            audioLengthTextView.text = time

        }

        this.setParams(audioMsgContainer, audioMsgContentContainer)
        this.setupDateAndSender(audioMsgTime, audioMsgSender, audioMsgLL)
        audioMsgContentContainer.setOnClickListener(this)
    }

    private fun setupColors(isSender: Boolean) {
        if (isSender) {
            audioCurrentTimeIndicator.alpha = 0.3f
            audioActionImg.setColorFilter(ContextCompat.getColor(context, R.color.icon_color_grey_light),
                    android.graphics.PorterDuff.Mode.SRC_IN)
        } else {
            audioCurrentTimeIndicator.alpha = 0.1f
            audioActionImg.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary),
                    android.graphics.PorterDuff.Mode.SRC_IN)
        }
        this.setTextColor(audioLengthTextView)
    }

    private fun setupDurationListener() {
        val max = AudioHelper.exoPlayer?.duration ?: 0L
        durationDisposable = Observable.just(Log.d(tag, "duration listener started"))
                .repeatWhen { completed ->
                    completed.delay(30, TimeUnit.MILLISECONDS)
                }
                .doOnSubscribe {
                    AudioHelper.durationDisposables?.add(it)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ _ ->
                    val current = AudioHelper.exoPlayer?.currentPosition?.toFloat()
                    val percentDecimal = current?.div(max)
                    percentDecimal?.let {
                        AudioHelper.currentTimeVisualization(it, audioCurrentTimeIndicator, audioMsgContainer)
                    }
                }, {})
    }

    override fun onClick(v: View?) {
        when (v) {
            audioMsgContentContainer -> {
                playAudio()
            }
        }
    }

    private fun playAudio() {
        AudioHelper.exoPlayer?.let { player ->
            // if is playing
            if (AudioHelper.currentMMData?.id == mmData?.id) {
                when (player.playbackState) {
                    Player.STATE_READY -> {
                        if (player.playWhenReady) {
                            // pause
                            AudioHelper.pauseExoPlayer()
                            showPauseButton(false)
                        } else {
                            // resume
                            AudioHelper.resumeExoPlayer()
                            showPauseButton(true)
                        }
                    }
                    Player.STATE_ENDED -> {
                        // restart
                        AudioHelper.exoPlayer
                        AudioHelper.restartExoPlayer()
                        showPauseButton(true)
                    }
                    else -> {
                    }
                }
            } else {
                AudioHelper.releaseExoPlayer()
                setupAndStartExoPlayer()
            }
        } ?: kotlin.run {
            setupAndStartExoPlayer()
        }
    }

    private fun setupAndStartExoPlayer() {
        this.mmData?.let { AudioHelper.setupExoPlayer(context, it) }
        exoPlayerListener()
        showPauseButton(true)
    }

    private fun exoPlayerListener() {
        AudioHelper.currentExoPlayerListenerDisposable = AudioHelper.getExoPlayerEventListener()
                ?.doOnDispose {
                    resetView()
                }
                ?.subscribe({ event ->
                    when (event) {
                        is PlayerStateChangedEvent -> {
                            when (event.playbackState) {
                                Player.STATE_ENDED -> {
                                    resetView()
                                    AudioHelper.completeExoPlayer()
                                }
                                Player.STATE_READY -> {
                                    if (durationDisposable?.isDisposed != false) {
                                        setupDurationListener()
                                    }
                                }
                                else -> {
                                    event.playbackState
                                }
                            }
                        }
                    }
                }, {})
    }

    private fun resetView() {
        showPauseButton(false)
        if (durationDisposable?.isDisposed == false) {
            durationDisposable?.dispose()
        }
        durationDisposable = null
        AudioHelper.currentTimeVisualization(0f, audioCurrentTimeIndicator, audioMsgContainer)
    }

    private fun showPauseButton(shouldShowPause: Boolean) {
        val img = if (shouldShowPause) {
            R.drawable.ic_pause
        } else {
            R.drawable.ic_play_arrow
        }
        Glide.with(this)
                .load(img)
                .into(audioActionImg)
    }

}