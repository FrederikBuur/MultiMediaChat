package com.buur.frederik.multimediechat.models

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import java.io.File

open class MMData(
        var id: Long,
        var source: String,
        val type: Int,
        var sender_id: Long? = null,
        var sender_name: String? = null,
        var size: Long? = null,
        var date: Long? = null,
        var is_send: Boolean? = null
) {
    companion object {

        fun deleteFile(filePath: String) {
            val file = File(filePath)
            if (file.exists()) {
                file.delete()
                Log.d("MMData", "deleted file $filePath")
            }
        }

        fun isFileTooBig(path: String?, size: Int): Boolean {
            val file = File(path)
            return if (file.exists()) {
                val l = file.length()
                l >= size
            } else {
                true
            }
        }

    }

}