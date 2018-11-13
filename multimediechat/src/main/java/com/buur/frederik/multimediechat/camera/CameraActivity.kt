package com.buur.frederik.multimediechat.camera

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.buur.frederik.multimediechat.R
import com.buur.frederik.multimediechat.enums.MMDataType
import com.buur.frederik.multimediechat.helpers.AudioHelper
import com.buur.frederik.multimediechat.messageviews.DocumentView
import com.jakewharton.rxbinding2.view.touches
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import com.wonderkiln.camerakit.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_camera.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.TimeUnit

class CameraActivity : RxAppCompatActivity(), View.OnClickListener {

    private val tag = "CameraActivity"
    private val captureDebounceDuration = 500L
    private val videoIndicatorTimerInterval = 50L // millisecond
    private val videoMaxDuration = 10000L // 10 sec

    private var capturePublishSubject: PublishSubject<Int>? = null
    private var captureDisposable: Disposable? = null
    private var videoLengthDisposable: Disposable? = null

    var shouldDeleteFile = true

    private var shouldRecordVideo = false
    private var isRecordingVideo = false

    init {
        capturePublishSubject = PublishSubject.create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        setupView()
    }

    private fun setupView() {
        setupCaptureTouchListener()
        setupCameraListener()
        if (captureDisposable?.isDisposed != false) {
            setupCapturePublisher()
        }
        AudioHelper.currentTimeVisualization(0f, cameraVideoLengthIndicator, cameraActivityContainer)

        cameraFacingDirectionButton.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            cameraFacingDirectionButton -> {
                flipCameraFacingDirection()
            }
        }
    }

    private fun setupCaptureTouchListener() {
        val disp = cameraCaptureButton.touches()
                .compose(bindToLifecycle())
                .doOnNext { motionEvent ->
                    when (motionEvent.action) {
                        MotionEvent.ACTION_DOWN -> {
                            capturePublishSubject?.onNext(1)
                            shouldRecordVideo = true
                        }
                        MotionEvent.ACTION_UP -> {
                            if (shouldRecordVideo && isRecordingVideo) {
                                cameraKitView.stopVideo()
                            } else {
                                cameraKitView.captureImage()
                            }
                            shouldRecordVideo = false
                            isRecordingVideo = false
                        }
                    }
                }
                .retry()
                .subscribe({}, {})
    }

    private fun setupCapturePublisher() {
        this.captureDisposable = capturePublishSubject
                ?.compose(bindToLifecycle())
                ?.debounce(captureDebounceDuration, TimeUnit.MILLISECONDS)
                ?.doOnNext {
                    if (shouldRecordVideo) {
                        isRecordingVideo = true
                        cameraKitView.captureVideo()
                        startVideoRecordingTimer()
                    }
                }
                ?.subscribe({}, {})
    }

    private fun startVideoRecordingTimer() {
        val take = videoMaxDuration / videoIndicatorTimerInterval
        videoLengthDisposable = Observable.interval(videoIndicatorTimerInterval, TimeUnit.MILLISECONDS)
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .take(take)
                .map {
                    it + 1
                }
                .doOnComplete {
                    cameraKitView.stopVideo()
                }
                .subscribe({ time ->
                    val percentDecimal = (time.toFloat().div(this.videoMaxDuration)).times(this.videoIndicatorTimerInterval)
                    AudioHelper.currentTimeVisualization(percentDecimal, cameraVideoLengthIndicator, cameraActivityContainer)
                }, {})
    }

    private fun setupCameraListener() {
        cameraKitView.addCameraKitListener(object : CameraKitEventListener {
            override fun onImage(image: CameraKitImage?) {
                val photoName = "/MultiMediaPhoto_${System.currentTimeMillis()}.jpg"
                val path = Environment.getExternalStorageDirectory().absolutePath
                val imageFile = File(path, photoName)
                try {
                    FileOutputStream(imageFile).use { fos ->
                        fos.write(image?.jpeg)
                    }
                    previewCapturedContent(imageFile.absolutePath, MMDataType.Image.ordinal)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun onVideo(video: CameraKitVideo?) {
                val videoName = "/MultiMediaVideo_${System.currentTimeMillis()}.mp4"
                val path = Environment.getExternalStorageDirectory().absolutePath
                val videoFile = File(path, videoName)
                try {
                    video?.videoFile?.renameTo(videoFile)
                    previewCapturedContent(videoFile.absolutePath, MMDataType.Video.ordinal)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun onEvent(e: CameraKitEvent?) {
            }

            override fun onError(error: CameraKitError?) {
                error?.exception?.printStackTrace()
            }
        })
    }

    private fun previewCapturedContent(content: String, type: Int) {

        if (videoLengthDisposable?.isDisposed == false) {
            videoLengthDisposable?.dispose()
        }

        val fragment = ContentPreviewFragment()
        val bundle = Bundle()
        bundle.putString("content", content)
        bundle.putInt("type", type)
        fragment.arguments = bundle

        val supFragMan = supportFragmentManager.beginTransaction()
        cameraFragmentContainer?.let { supFragMan.add(cameraFragmentContainer.id, fragment, fragment.javaClass.toString()) }
        supFragMan.addToBackStack(fragment.tag)
        supFragMan.commit()
    }

    private fun flipCameraFacingDirection() {
        cameraKitView.toggleFacing()
    }

    private fun previewFragmentOnPause() {
        if (supportFragmentManager.backStackEntryCount == 0) {
            super.onBackPressed()
            return
        }
        AudioHelper.currentTimeVisualization(0f, cameraVideoLengthIndicator, cameraActivityContainer)

        val tag = supportFragmentManager.getBackStackEntryAt(supportFragmentManager.backStackEntryCount - 1).name
        val fragment = supportFragmentManager.findFragmentByTag(tag)

        (fragment as? ContentPreviewFragment)?.let {
            if (shouldDeleteFile) {
                it.handleBackPress()
            }
        } ?: kotlin.run {
            super.onBackPressed()
        }
    }

    fun returnIntentWithResult(content: String, type: Int ) {
        val returnIntent = Intent()
        returnIntent.putExtra(CAMERA_CONTENT_KEY, content)
        returnIntent.putExtra(CAMERA_TYPE_KEY, type.toString())
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

    override fun onBackPressed() {
        previewFragmentOnPause()
    }

    override fun onResume() {
        super.onResume()
        cameraKitView.start()
        AudioHelper.currentTimeVisualization(0f, cameraVideoLengthIndicator, cameraActivityContainer)
    }

    override fun onPause() {
        super.onPause()
        cameraKitView.stop()
        previewFragmentOnPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (captureDisposable?.isDisposed == false) {
            captureDisposable?.dispose()
        }
        if (videoLengthDisposable?.isDisposed == false) {
            videoLengthDisposable?.dispose()
        }

    }

    companion object {
        const val CAMERA_CONTENT_KEY = "camera_content"
        const val CAMERA_TYPE_KEY = "camera_type"
    }

}
