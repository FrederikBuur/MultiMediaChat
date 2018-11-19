package com.buur.frederik.multimediechat.helpers

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import com.buur.frederik.multimediechat.models.MMData
import com.github.satoshun.reactivex.exoplayer2.PlayerEvent
import com.github.satoshun.reactivex.exoplayer2.events
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

object AudioHelper {

//    var mediaPlayer: MediaPlayer? = null
//    var currentAudioFile: MMData? = null
//    var currentAudioTime: Float? = null

    var exoPlayer: SimpleExoPlayer? = null
    var currentTime: Long? = null
    var currentMMData: MMData? = null
    var currentExoPlayerListenerDisposable: Disposable? = null
    var durationDisposables: CompositeDisposable? = null

    fun currentTimeVisualization(percentDecimal: Float, audioTimeIndicator: FrameLayout, audioIndicatorContainer: View) {
        val params = audioTimeIndicator.layoutParams as FrameLayout.LayoutParams
        val containerWidth = audioIndicatorContainer.width
        params.width = (percentDecimal * containerWidth).toInt()
        audioTimeIndicator.layoutParams = params
    }

    fun setupExoPlayer(context: Context, mmData: MMData) {
        if (durationDisposables == null) {
            durationDisposables = CompositeDisposable()
        }
        this.currentMMData = mmData
        val renderersFactory = DefaultRenderersFactory(context,
                null,
                DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF)
        val trackSelector = DefaultTrackSelector()
        exoPlayer = ExoPlayerFactory.newSimpleInstance(
                context,
                renderersFactory,
                trackSelector
        )
        val userAgent = Util.getUserAgent(context, "ExoPlayerMM")
        val mediaSource = ExtractorMediaSource(
                Uri.parse(mmData.source),
                DefaultDataSourceFactory(context, userAgent),
                DefaultExtractorsFactory(),
                null,
                null
        )
        exoPlayer?.prepare(mediaSource)
        exoPlayer?.playWhenReady = true

    }

    fun getExoPlayerEventListener(): Observable<out PlayerEvent>? {
        return exoPlayer?.events()
    }

    fun resumeExoPlayer() {
            exoPlayer?.playWhenReady = true
    }

    fun completeExoPlayer() {
        exoPlayer?.seekTo(0)
        exoPlayer?.playWhenReady = false
    }

    fun restartExoPlayer() {
        exoPlayer?.seekTo(0)
        exoPlayer?.playWhenReady = true
    }

    fun pauseExoPlayer() {
        exoPlayer?.playWhenReady = false
    }

    fun releaseExoPlayer() {
        if (currentExoPlayerListenerDisposable?.isDisposed == false) {
            currentExoPlayerListenerDisposable?.dispose()
        }
        currentExoPlayerListenerDisposable = null
        if (durationDisposables?.isDisposed == false) {
            durationDisposables?.dispose()
        }
        durationDisposables = null
        exoPlayer?.playWhenReady = false
        exoPlayer?.release()
        exoPlayer = null
    }

}