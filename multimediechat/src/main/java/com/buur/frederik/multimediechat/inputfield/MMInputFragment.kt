package com.buur.frederik.multimediechat.inputfield

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.os.SystemClock
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import com.buur.frederik.multimediechat.R
import com.buur.frederik.multimediechat.camera.CameraActivity
import com.buur.frederik.multimediechat.enums.MMDataType
import com.buur.frederik.multimediechat.helpers.AudioHelper
import com.buur.frederik.multimediechat.helpers.UploadHelper
import com.buur.frederik.multimediechat.models.MMData
import com.buur.frederik.multimediechat.helpers.PermissionRequester
import com.buur.frederik.multimediechat.gifpicker.GifPickerActivity
import com.buur.frederik.multimediechat.helpers.MMToast
import com.jakewharton.rxbinding2.view.touches
import com.jakewharton.rxbinding2.widget.textChanges
import com.trello.rxlifecycle2.components.support.RxFragment
import com.vincent.filepicker.Constant
import com.vincent.filepicker.filter.entity.NormalFile
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.view_mm_input_field.*
import kotlinx.android.synthetic.main.view_options.*
import java.io.IOException
import java.lang.IllegalStateException
import java.lang.RuntimeException


class MMInputFragment : RxFragment(), View.OnClickListener {

    private val tagg = "MMInputFragment"
    private val fileSizeLimit = 15000000
    private val recordingMaxLength: Long = 60000 // millisecond
    private val recordingTimerInterval: Long = 50 // millisecond

    private var mmInputController: MMInputController? = null

    private var isAudioButtonActivated: Boolean? = null
    private var discardRecording: Boolean? = null

    private var delegate: ISendMessage? = null

    private var mediaRecorder: MediaRecorder? = null
    private var outputAudioFile: String? = null
    private var recordingTimerDisposable: Disposable? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.view_mm_input_field, container, true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && data != null) {

            when (requestCode) {
                // gif request code
                MMInputFragment.GIF_REQUEST_CODE -> {
                    val gifUrl = data.getStringExtra(GifPickerActivity.GIF_KEY)
                    convertToMMDataAndSend(gifUrl, MMDataType.Gif)
                }
                // gallery request code
                MMInputFragment.GALLERY_REQUEST_CODE -> {
                    val image = data.data
                    image?.let {
                        val path = UploadHelper.getPathFromURI(it, context)
                        if (it.toString().contains("/video/")) {
                            if (!MMData.isFileTooBig(path, fileSizeLimit)) {
                                convertToMMDataAndSend(path ?: "", MMDataType.Video)
                            } else {
                                MMToast.showToast(context, "File too large", Toast.LENGTH_SHORT)
                            }
                        } else {
                            convertToMMDataAndSend(path ?: "", MMDataType.Image)
                        }
                    }
                }
                // camera request code
                MMInputFragment.CAMERA_REQUEST_CODE -> {
                    val content = data.getStringExtra(CameraActivity.CAMERA_CONTENT_KEY)
                    val type = data.getStringExtra(CameraActivity.CAMERA_TYPE_KEY).toIntOrNull() ?: -1
                    val mmType =
                            if (type == MMDataType.Video.ordinal) {
                        MMDataType.Video
                    } else {
                        MMDataType.Image
                    }
                    convertToMMDataAndSend(content, mmType)
                }
                // document request code
                MMInputFragment.DOCUMENT_REQUEST_CODE -> {
                    val file = data.getParcelableArrayListExtra<NormalFile>(Constant.RESULT_PICK_FILE).firstOrNull()?.path
                    file?.let { path ->
                        if (!MMData.isFileTooBig(path, fileSizeLimit)) {
                            convertToMMDataAndSend(path, MMDataType.Document)
                        } else {
                            MMToast.showToast(context, "File too large", Toast.LENGTH_SHORT)
                        }
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun setup(delegate: ISendMessage) {
        this.delegate = delegate

        mediaSelectionView.setup( this)
        AudioHelper.currentTimeVisualization(0f, recordMessageNotificationIndicator, recordMessageNotification)

        if (mmInputController == null) {
            mmInputController = MMInputController()
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            optionsViewCamera -> {
                mediaSelectionView.openCamera(CAMERA_REQUEST_CODE)
            }
            optionsViewFile -> {
                mediaSelectionView.openDocumentPicker(DOCUMENT_REQUEST_CODE)
            }
            optionsViewGif -> {
                mediaSelectionView.openGifPicker(GIF_REQUEST_CODE)

            }
            optionsViewGallery -> {
                mediaSelectionView.openGalleryPicker(GALLERY_REQUEST_CODE)
            }
            else -> {
            }
        }
    }

    private fun setupListeners() {

        optionsViewCamera.setOnClickListener(this)
        optionsViewGif.setOnClickListener(this)
        optionsViewFile.setOnClickListener(this)
        optionsViewGallery.setOnClickListener(this)

        val disposeable = inputEditText.textChanges()
                .compose(bindToLifecycle())
                .doOnNext { editText ->
                    val newDrawable: Drawable? = if (editText.trim().isNotEmpty()) {
                        // show send text
                        sendButtonImgView.scaleY = -1f
                        isAudioButtonActivated = false
                        context?.let { ContextCompat.getDrawable(it, R.drawable.ic_round_send) }
                    } else {
                        // show mic
                        sendButtonImgView.scaleY = 1f
                        isAudioButtonActivated = true
                        context?.let { ContextCompat.getDrawable(it, R.drawable.ic_mic) }
                    }
                    val currentDrawable = sendButtonImgView.drawable
                    // check if necessary to set new drawable
                    if (currentDrawable.constantState != newDrawable?.constantState) {
                        sendButtonImgView.setImageDrawable(newDrawable)
                    }
                }
                .subscribe({}, {})

        val disposable1 = sendButton.touches()
                .compose(bindToLifecycle())
                .doOnNext { motionEvent ->
                    when (motionEvent.action) {
                        MotionEvent.ACTION_DOWN -> {

                            if (inputEditText.text.toString().trim().isEmpty()) {
                                discardRecording = false
                                startRecording()
                            }
                        }
                        MotionEvent.ACTION_UP -> {
                            if (inputEditText.text.trim().isNotEmpty()) {
                                sendTextMessage()
                            } else {
                                discardRecording = mmInputController?.isTouchOutsideView(motionEvent, sendButton)
                                stopRecording()
                                sendOrDiscardRecording(motionEvent)
                            }
                        }
                        MotionEvent.ACTION_MOVE -> {
                            if (inputEditText.text.toString().trim().isEmpty()) {
                                // check is touch is outside of
                                val isOutside = mmInputController?.isTouchOutsideView(motionEvent, sendButton)
                                updateRecordNotification(true, isOutside != true)
                            }
                        }
                    }
                }
                .retry()
                .subscribe({}, {
                })

    }

    private fun updateRecordNotification(show: Boolean, showReleaseToSendText: Boolean? = null) {
        if (show) {
            recordMessageNotification.visibility = View.VISIBLE
            if (showReleaseToSendText != true) {
                // show release to discard
                recordMessageTV.text = "Release to cancel"
            } else {
                // show release to send
                recordMessageTV.text = "Drag to cancel, release to send"
            }
        } else {
            recordMessageNotification.visibility = View.GONE
            AudioHelper.currentTimeVisualization(0f, recordMessageNotificationIndicator, recordMessageNotification)
        }
    }



    private fun sendOrDiscardRecording(motionEvent: MotionEvent) {
        if (discardRecording == false) {
            val downTime = SystemClock.uptimeMillis() - motionEvent.downTime
            if (downTime < 250) {
                discardRecording = mmInputController?.showHoldToRecordToast(context)
            } else {
                outputAudioFile?.let { convertToMMDataAndSend(it, MMDataType.Audio) }
            }

        } else {
            // discard recording
            discardRecording = false
            outputAudioFile?.let { MMData.deleteFile(it) }
        }
    }

    private fun startRecording() {
        context?.let { con ->
            if (!PermissionRequester.isMicrophonePermissionGranted(con)) {
                PermissionRequester.requestPermissions(con as? AppCompatActivity, Manifest.permission.RECORD_AUDIO)
                return
            }
        }

        val audioName = "/MultiMediaAudio_${System.currentTimeMillis()}.3gp"
        outputAudioFile = Environment.getExternalStorageDirectory().absolutePath + audioName

        mediaRecorder = MediaRecorder()
        mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        mediaRecorder?.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB)
        mediaRecorder?.setOutputFile(outputAudioFile)

        try {
            mediaRecorder?.prepare()
            mediaRecorder?.start()
            updateRecordNotification(true)
            this.recordingTimerDisposable = mmInputController?.startRecordingTimeCounter(this.recordingTimerInterval, this.recordingMaxLength)
                    ?.compose(bindToLifecycle())
                    ?.subscribeOn(Schedulers.computation())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.doOnComplete {
                        stopRecording(false)
                    }
                    ?.subscribe({ time ->
                        val percentDecimal = (time.toFloat().div(this.recordingMaxLength)).times(this.recordingTimerInterval)
                        AudioHelper.currentTimeVisualization(percentDecimal, recordMessageNotificationIndicator, recordMessageNotification)
                    }, {})
        } catch (ise: IllegalStateException) {
            Log.d("Error", ise.message)
        } catch (ioe: IOException) {
            Log.d("Error", ioe.message)
        }
    }

    private fun stopRecording(shouldUpdateNotification: Boolean = true) {
        try {
            mediaRecorder?.stop()
            mediaRecorder?.release()
            mediaRecorder = null

        } catch (re: RuntimeException) {
            this.discardRecording = mmInputController?.showHoldToRecordToast(context)
            re.message
        }
        if (shouldUpdateNotification) {
            updateRecordNotification(false)
        }
        if (recordingTimerDisposable?.isDisposed == false) {
            recordingTimerDisposable?.dispose()
        }
    }

    // takes data, converts into MMData, calls send
    private fun convertToMMDataAndSend(data: String, type: MMDataType) {
        val id = System.currentTimeMillis()
        delegate?.sendMMData(MMData(id, data, type.ordinal))
    }

    // takes text from edit text and sends to mmData converter
    private fun sendTextMessage() {
        updateRecordNotification(false)
        val textMessage = inputEditText.text.toString().trim()
        if (textMessage.isEmpty()) return
        inputEditText.text.clear()
        convertToMMDataAndSend(textMessage, MMDataType.Text)
    }

    fun getEditText(): EditText {
        return inputEditText
    }

    override fun onStop() {
        super.onStop()

        AudioHelper.releaseExoPlayer()
        mediaRecorder?.release()
        mediaRecorder = null
    }

    companion object {
        const val GIF_REQUEST_CODE = 5492
        const val GALLERY_REQUEST_CODE = 4753
        const val CAMERA_REQUEST_CODE = 8925
        const val DOCUMENT_REQUEST_CODE = Constant.REQUEST_CODE_PICK_FILE //6286

        fun getMMInputFieldInstance(childFragmentManager: FragmentManager, fragmentId: Int): MMInputFragment? {
            return childFragmentManager.findFragmentById(fragmentId) as? MMInputFragment
        }

    }

}