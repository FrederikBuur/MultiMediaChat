package com.buur.frederik.multimediechat.helpers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.support.media.ExifInterface
import android.util.Base64
import com.buur.frederik.multimediechat.models.MMData
import com.google.gson.Gson
import io.reactivex.Observable
import java.io.ByteArrayOutputStream
import java.io.File
import android.provider.MediaStore
import android.util.Log
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.FileInputStream
import java.io.FileOutputStream
import java.lang.Exception


object ImageHelper {

    private const val IMAGE_MAX_SIZE = 1500

    private fun rotateBitmapIfNeeded(uriPath: String, bitmap: Bitmap?): Bitmap? {

        val ei = ExifInterface(uriPath)
        val orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        val matrix = Matrix()

        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> {
                matrix.postRotate(90f)
            }
            ExifInterface.ORIENTATION_ROTATE_180 -> {
                matrix.postRotate(180f)
            }
            ExifInterface.ORIENTATION_ROTATE_270 -> {
                matrix.postRotate(270f)
            }
        }

        return if (orientation == ExifInterface.ORIENTATION_UNDEFINED || orientation == ExifInterface.ORIENTATION_NORMAL) {
            bitmap
        } else {
            bitmap?.let { Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true) }
        }
    }

    fun prepareImagePathToUpload(mmData: MMData): Observable<MultipartBody.Part> {

        return Observable.create { emitter ->

            val file = File(mmData.source)
            if (!file.exists()) {
                Log.d("", "")
            }
            val bitmap = ImageHelper.rotateBitmapIfNeeded(file.path, ImageHelper.downscaleFile(file))

            val tempFile = File.createTempFile("image-", ".jpg")
            FileOutputStream(tempFile).use { stream ->
                bitmap?.compress(Bitmap.CompressFormat.JPEG, 75, stream)
            }
            val reqFile = RequestBody.create(MediaType.parse("image/*"), tempFile)
            val body = MultipartBody.Part.createFormData("image", tempFile.name, reqFile)
            if (!emitter.isDisposed) {
                emitter.onNext(body)
                emitter.onComplete()
            }
        }
    }

    // https://stackoverflow.com/questions/11732872/android-how-can-i-call-camera-or-gallery-intent-together
    fun getPathFromURI(uri: Uri, context: Context?): String? {
        val filePath = arrayOf(MediaStore.Images.Media.DATA)
        val c = context?.contentResolver?.query(uri, filePath,
                null, null, null)
        c?.moveToFirst()
        val columnIndex = c?.getColumnIndex(filePath[0])
        val selectedImagePath = c?.getString(columnIndex ?: 1)
        c?.close()

        return selectedImagePath
    }

    private fun downscaleFile(f: File): Bitmap? {

        //Decode image size
        val o = BitmapFactory.Options()
        o.inJustDecodeBounds = true

        var fis = FileInputStream(f)
        BitmapFactory.decodeStream(fis, null, o)
        fis.close()

        var scale = 1
        if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
            scale = Math.pow(2.0, Math.ceil(Math.log(IMAGE_MAX_SIZE / Math.max(o.outHeight, o.outWidth).toDouble()) / Math.log(0.5)).toInt().toDouble()).toInt()
        }

        //Decode with inSampleSize
        val o2 = BitmapFactory.Options()
        o2.inSampleSize = scale
        fis = FileInputStream(f)
        val bitmap = BitmapFactory.decodeStream(fis, null, o2)
        fis.close()

        return bitmap
    }

}