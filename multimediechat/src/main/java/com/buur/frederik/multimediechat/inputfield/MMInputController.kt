package com.buur.frederik.multimediechat.inputfield

import android.content.Context
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

class MMInputController {

    fun startRecordingTimeCounter(interval: Long, maxLength: Long): Observable<Long> {

        return Observable.interval(interval, TimeUnit.MILLISECONDS)
                .take(maxLength)
                .map {
                    it + 1
                }
                .doOnNext {
                }
    }

    fun isTouchOutsideView(motionEvent: MotionEvent, view: View): Boolean {
        // checks if touch is outside of view bounds
        val threshold = 10
        return (motionEvent.x < 0.minus(threshold) ||
                motionEvent.y < 0.minus(threshold) ||
                motionEvent.x > view.measuredWidth.plus(threshold) ||
                motionEvent.y > view.measuredHeight.plus(threshold))
    }

    fun showHoldToRecordToast(context: Context?): Boolean {
        Toast.makeText(context, "Hold to record a message", Toast.LENGTH_SHORT).show()
        return true
    }

}