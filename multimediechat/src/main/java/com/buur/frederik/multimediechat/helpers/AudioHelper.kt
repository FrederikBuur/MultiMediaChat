package com.buur.frederik.multimediechat.helpers

import android.content.Context
import android.net.Uri
import android.util.Base64
import io.reactivex.Observable
import java.io.*
import java.lang.Exception

object AudioHelper {

    fun convertStringByteArrayTo3gp(outputFile: String?, stringByteArray: String?): Observable<ByteArray> {

        return Observable.create { emitter ->

            try {
                val path = File(outputFile)
                // convert string to byte array
                val byteArray = Base64.decode(stringByteArray, Base64.DEFAULT)

                val fos = FileOutputStream(path)
                fos.write(byteArray)
                fos.close()
                emitter.onNext(byteArray)
                emitter.onComplete()
            } catch (e: Exception) {
                emitter.onError(Throwable("ops"))
                emitter.onComplete()
            }

        }

    }

    fun convert3gpToString(context: Context?, outputFile: String?): Observable<String> {

        return Observable.create { emitter ->

            // convert 3gp to byte array
            var soundBytes: ByteArray
            try {
                val inputStream = context?.contentResolver?.openInputStream(Uri.fromFile(File(outputFile)))
                inputStream?.let {
                    soundBytes = ByteArray(it.available())
                    soundBytes = inputStreamToByteArray(it)

                    // convert byte array to string
                    val bytesAsString = Base64.encodeToString(soundBytes, Base64.DEFAULT)

                    // emit converted byte array
                    emitter.onNext(bytesAsString)
                    emitter.onComplete()
                }
            } catch (e: Exception) {
                e.message
                emitter.onError(Throwable(e.message))
                emitter.onComplete()
            }

        }
    }

    private fun inputStreamToByteArray(inputStream: InputStream): ByteArray {
        val out = ByteArrayOutputStream()
        var read = 0
        val buffer = ByteArray(1024)
        while (read != -1) {
            read = inputStream.read(buffer)
            if (read != -1) {
                out.write(buffer, 0, read)
            }
        }
        out.close()
        return out.toByteArray()
    }

}