package com.buur.frederik.multimediechat.helpers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.support.media.ExifInterface
import com.buur.frederik.multimediechat.models.MMData
import io.reactivex.Observable
import java.io.File
import android.provider.MediaStore
import android.util.Log
import com.buur.frederik.multimediechat.api.ProgressRequestBody
import com.buur.frederik.multimediechat.enums.MMDataType
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.FileInputStream
import java.io.FileOutputStream


object UploadHelper {

    private const val IMAGE_MAX_SIZE = 1500
    private val tag = "UploadHelper"

    // for images
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

    // converting mmData to upload object
    fun prepareMMDataToUpload(mmData: MMData): Observable<MultipartBody.Part> {
        return Observable.create { emitter ->
            var reqBody: RequestBody? = null
            var partName: String? = null
            var fileName: String? = null
            val file = File(mmData.source)
            if (!file.exists()) {
                emitter.onError(Throwable("files doesn't exists"))
                emitter.onComplete()
            }

            when (mmData.type) {
                MMDataType.Image.ordinal -> {
                    val bitmap = rotateBitmapIfNeeded(file.path, UploadHelper.downscaleFile(file))
                    val tempFile = File.createTempFile("MultiMediaImage_", ".jpg")
                    FileOutputStream(tempFile).use { stream ->
                        bitmap?.compress(Bitmap.CompressFormat.JPEG, 75, stream)
                    }
                    reqBody = RequestBody.create(MediaType.parse(mmData.source), tempFile)
                    partName = "image"
                    fileName = tempFile.name
                }
                MMDataType.Video.ordinal -> {
                    reqBody = RequestBody.create(MediaType.parse(mmData.source), file)
                    partName = "video"
                    fileName = "MultiMediaVideo_${file.name}"
                }
                MMDataType.Audio.ordinal -> {
                    reqBody = RequestBody.create(MediaType.parse(mmData.source), file)
                    partName = "audio"
                    fileName = "MultiMediaAudio_${file.name}"
                }
                MMDataType.Document.ordinal -> {
                    reqBody = RequestBody.create(MediaType.parse(mmData.source), file)
                    partName = "document"
                    fileName = "MultiMediaDocument_${file.name}"
                }
                else -> {
                    emitter.onError(Throwable("Unsupported"))
                    emitter.onComplete()
                }
            }

            if (partName != null && fileName != null && reqBody != null) {
                val bodyPart = MultipartBody.Part.createFormData(partName, fileName, reqBody)
                if (!emitter.isDisposed) {
                    emitter.onNext(bodyPart)
                    emitter.onComplete()
                }
            }
        }
    }

//    fun prepareVideoToUpload(mmData: MMData) : Observable<Any> {
//        return Observable.create { emitter ->
//            val file = File(mmData.source)
//            if (!file.exists()) {
//                emitter.onError(Throwable("files doesn't exists"))
//                emitter.onComplete()
//            }
////            val reqBody = RequestBody.create(MediaType.parse(mmData.source), file)
//            val reqBody = ProgressRequestBody(file)
//            reqBody.getProgressSubject()
//                    .doOnDispose {
//                        Log.d("", "")
//                    }
//                    .subscribe({ progress ->
//                        Log.d(tag, "Upload progress: $progress")
//                    }, {
//                        it
//                    })
//            val bodyPart = MultipartBody.Part.createFormData("video", file.name, reqBody)
//            if (!emitter.isDisposed) {
//                emitter.onNext(bodyPart)
//                emitter.onComplete()
//            }
//        }
//    }

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

    // to downscale big files, such as images
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