package com.buur.frederik.multimediechat.api

import android.util.Log
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.File
import java.io.FileInputStream

class ProgressRequestBody : RequestBody {

    private val tag = "ProgressRequestBody"

    private val file: File
    private val progressSubject: PublishSubject<Float> = PublishSubject.create<Float>()

    constructor(file: File) : super() {
        this.file = file
    }

    fun getProgressSubject(): Observable<Float> {
        return progressSubject
    }

    override fun contentType(): MediaType? {
        return MediaType.parse("video/*")
    }

    override fun contentLength(): Long {
        return file.length()
    }

    override fun writeTo(sink: BufferedSink) {
        val fileLength = file.length()
        val buffer = ByteArray(2048)
        val inputStream = FileInputStream(file)
        var uploaded: Long = 0

        inputStream.use { innerInputStream ->
            var read: Int
            var lastProgressUpdate = 0f
            read = innerInputStream.read(buffer)

            while (read != -1) {
                uploaded += read.toLong()
                sink.write(buffer, 0, read)
                read = innerInputStream.read(buffer)

                val progress = (uploaded.toFloat() / fileLength.toFloat()) * 100f
                if (progress - lastProgressUpdate > 1 || progress >= 100f) {
                    progressSubject.onNext(progress)
                    if (progress >= 100f) {
                        progressSubject.onComplete()
                    }
                    lastProgressUpdate = progress
                }
            }
        }

    }
}