package com.buur.frederik.multimediechat.helpers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.provider.MediaStore
import android.support.media.ExifInterface
import android.util.Base64
import io.reactivex.Observable
import java.io.ByteArrayOutputStream
import java.io.File



object ImageHelper {

    fun convertUriStringToBitmapString(uri: String, context: Context?): Observable<String>  {
        return Observable.create { emitter ->

            val imageUri = Uri.fromFile(File(uri))
            val bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, imageUri)

            imageUri.path?.let { path ->
                val rotatedBitmap = rotateBitmapIfNeeded(path, bitmap) // only for images?

                val outputStream = ByteArrayOutputStream()
                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                val b = outputStream.toByteArray()
                val asString = Base64.encodeToString(b, Base64.DEFAULT)

                if (!emitter.isDisposed) {
                    emitter.onNext(asString)
                    emitter.onComplete()
                }
            } ?: kotlin.run {
                emitter.onError(Throwable("Path null"))
                emitter.onComplete()
                return@create
            }
        }
    }

    private fun rotateBitmapIfNeeded(uriPath: String, bitmap: Bitmap): Bitmap {

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
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        }
    }



    fun convertBitmapStringToBitmap(bitmapString: String?): Observable<Bitmap> {
        return Observable.create { emitter ->

            bitmapString?.let { bitString ->
                val encodeByte = Base64.decode(bitString, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)

                bitmap?.let { bm ->
                    if (!emitter.isDisposed) {
                        emitter.onNext(bm)
                        emitter.onComplete()
                    }
                } ?: kotlin.run {
                    emitter.onError(Throwable("Not a bitmap"))
                    emitter.onComplete()
                }
            } ?: kotlin.run {
                emitter.onError(Throwable("Bitmap String null"))
                emitter.onComplete()
            }
        }
    }

}