package com.buur.frederik.multimediechat.views.inputfield

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.os.SystemClock
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.buur.frederik.multimediechat.R
import com.buur.frederik.multimediechat.enums.MMDataType
import com.buur.frederik.multimediechat.helpers.AudioHelper
import com.buur.frederik.multimediechat.models.MMData
import com.buur.frederik.multimediechat.views.MMView
import com.buur.frederik.multimediechat.helpers.ImageHelper
import com.buur.frederik.multimediechat.helpers.PermissionRequester
import com.buur.frederik.multimediechat.views.gifpicker.GifPickerActivity
import com.jakewharton.rxbinding2.view.touches
import com.jakewharton.rxbinding2.widget.textChanges
import com.nguyenhoanglam.imagepicker.model.Config
import com.nguyenhoanglam.imagepicker.model.Image
import com.trello.rxlifecycle2.components.support.RxFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.view_mm_input_field.*
import kotlinx.android.synthetic.main.view_options.*
import java.io.IOException
import java.lang.Exception
import java.lang.IllegalStateException
import java.lang.RuntimeException


class MMInputFieldView: RxFragment(), View.OnClickListener {

    private var isAudioButtonActivated: Boolean? = null
    private var discardRecording: Boolean? = null

    private var mediaPlayer: MediaPlayer? = null
    private var mediaRecorder: MediaRecorder? = null
    private var outputAudioFile: String? = null

    private var callerContext: Context? = null
    private var mmView: View? = null
    private var delegate: ISendMessage? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.view_mm_input_field, container, true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // if result form image picker
        if (requestCode == MMInputFieldView.GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val image = data.getParcelableArrayListExtra<Image>(Config.EXTRA_IMAGES).first().path
            convertToMMDataAndSend(image, MMDataType.Image)
//            val disp = ImageHelper.convertUriStringToBitmapString(image, context)
//                    .subscribeOn(Schedulers.computation())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe({
//                        convertToMMDataAndSend(image, MMDataType.Image)
//                    }, {})
        }
        // if result from gif picker
        else if (requestCode == MMInputFieldView.GIF_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val gifUrl = data.getStringExtra(GifPickerActivity.GIF_KEY)
            convertToMMDataAndSend(gifUrl, MMDataType.Image)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun setup(callerContext: Context?, mmView: MMView, delegate: ISendMessage) {
        this.callerContext = callerContext
        this.mmView = mmView
        this.delegate = delegate

        inputOptionsView.setup(mmView, this)

        context?.let {
            if (!PermissionRequester.devicePermissionIsGranted(it)) {
                PermissionRequester.requestPermissions((callerContext as? AppCompatActivity))
            }
        }
    }

    override fun onClick(v: View?) {
                when (v) {
                    optionsViewCamera -> {
                        inputOptionsView.openImagePicker()
                    }
                    optionsViewFile -> {
                    }
                    optionsViewGif -> {
                        inputOptionsView.openGifPicker(context, MMInputFieldView.GIF_REQUEST_CODE)

                    }
                    else -> {
                    }
                }
    }

    private fun setupListeners() {

        optionsViewCamera.setOnClickListener(this)
        optionsViewGif.setOnClickListener(this)
        optionsViewFile.setOnClickListener(this)

        val disposeable = inputEditText.textChanges()
                .compose(bindToLifecycle())
                .doOnNext { editText ->
                    val newDrawable: Drawable? = if (editText.trim().isNotEmpty()) {
                        // show send text
                        sendButtonImgView.scaleY = -1f
                        isAudioButtonActivated = false
                        context?.let {ContextCompat.getDrawable(it, R.drawable.ic_round_send)}
                    } else {
                        // show mic
                        sendButtonImgView.scaleY = 1f
                        isAudioButtonActivated = true
                        context?.let {ContextCompat.getDrawable(it, R.drawable.ic_mic)}
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
                                discardRecording = isTouchOutsideView(motionEvent, sendButtonImgView)
                                stopRecording()
                                sendOrDiscardRecording(motionEvent)
                            }
                        }
                        MotionEvent.ACTION_MOVE-> {
                            // check is touch is outside of
                            val isOutside = isTouchOutsideView(motionEvent, sendButtonImgView)
                            updateRecordNotification(true, !isOutside)
                        }
                    }
                }
                .retry()
                .subscribe({}, {
                    it
                })

    }

    private fun updateRecordNotification(show: Boolean, showReleaseToSendText: Boolean? = null) {
        if (show) {
            recordMessageNotification.visibility = View.VISIBLE
            if (showReleaseToSendText != true) {
                // show release to discard
                recordMessageTV.text = "release to discard"
            } else {
                // show release to send
                recordMessageTV.text = "release to send"
            }
        } else {
            recordMessageNotification.visibility = View.GONE
        }
    }

    private fun sendOrDiscardRecording(motionEvent: MotionEvent) {
        if (discardRecording == false) {
            val downTime = SystemClock.uptimeMillis() - motionEvent.downTime
            if (downTime < 500) {
                showHoldToRecordToast()
            } else {
//                // convert and send recording
//                val disp = AudioHelper.convert3gpToString(context, outputAudioFile)
//                        .compose(bindToLifecycle())
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe({
//                            convertToMMDataAndSend(it, MMDataType.Audio)
//                        }, {
//                            it
//                        })
                outputAudioFile?.let { convertToMMDataAndSend(it, MMDataType.Audio) }
            }

        } else {
            // discard recording
            discardRecording = false
        }
    }

    private fun startRecording() {

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
        } catch (ise: IllegalStateException) {
            Log.d("Error", ise.message)
        } catch (ioe: IOException) {
            Log.d("Error", ioe.message)
        }

    }

    private fun stopRecording() {
        try {
            mediaRecorder?.stop()
            mediaRecorder?.release()
            mediaRecorder = null

        } catch (re: RuntimeException) {
            showHoldToRecordToast()
            re.message
        }
        updateRecordNotification(false)
    }

    private fun showHoldToRecordToast() {
        // show hold-to-record view
        Toast.makeText(context, "Hold to record a message", Toast.LENGTH_SHORT).show()
        discardRecording = true
    }

    private fun isTouchOutsideView(motionEvent: MotionEvent, view: View): Boolean {
        // checks if touch is outside of view bounds
        val threshold = 50
        return (motionEvent.x < 0.plus(threshold) ||
                motionEvent.y < 0.plus(threshold) ||
                motionEvent.x > view.measuredWidth.plus(threshold) ||
                motionEvent.y > view.measuredHeight.plus(threshold))
    }


    // takes data, converts into MMData, calls send
    private fun convertToMMDataAndSend(data: String, type: MMDataType) {
        when (type) {
            MMDataType.Text -> {
                val message = (data as? String) ?: "Something went wrong"
                delegate?.sendMMData(MMData(message, type.ordinal))
            }
            MMDataType.Image -> {
                delegate?.sendMMData(MMData(data, type.ordinal))
            }
            MMDataType.Audio -> {
                delegate?.sendMMData(MMData(data, type.ordinal))
            }
            MMDataType.Video -> {
            } //TODO()
        }
    }

    // takes text from edit text and sends to mmData converter
    private fun sendTextMessage() {
        val textMessage = inputEditText.text.toString().trim()
        if (textMessage.isEmpty()) return
        inputEditText.text.clear()
        convertToMMDataAndSend(textMessage, MMDataType.Text)
    }

    override fun onStop() {
        super.onStop()

        mediaRecorder?.release()
        mediaRecorder = null
        mediaPlayer?.release()
        mediaPlayer = null

    }

    companion object {

        const val INPUT_TEXT_KEY = "inputText"

        const val GALLERY_REQUEST_CODE = Config.RC_PICK_IMAGES
        const val GIF_REQUEST_CODE = 5492

        fun getMMInputFieldInstance(childFragmentManager: FragmentManager, fragmentId: Int): MMInputFieldView? {
            return childFragmentManager.findFragmentById(fragmentId) as? MMInputFieldView
        }

    }

}