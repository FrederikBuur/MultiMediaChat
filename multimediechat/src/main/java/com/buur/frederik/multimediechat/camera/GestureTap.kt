package com.buur.frederik.multimediechat.camera

import android.util.Log
import android.view.MotionEvent
import android.view.GestureDetector


internal class GestureTap : GestureDetector.SimpleOnGestureListener() {
    override fun onDoubleTap(e: MotionEvent): Boolean {

        return true
    }

    override fun onDown(e: MotionEvent?): Boolean {
        return true
    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {

        return true
    }

    override fun onLongPress(e: MotionEvent?) {
        Log.d("cameraActivity", "longpress")
        super.onLongPress(e)
    }
}