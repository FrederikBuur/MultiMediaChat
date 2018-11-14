package com.buur.frederik.multimediechat.models

import android.os.Parcel
import android.os.Parcelable

open class MMData(
        var id: Long,
        var source: String,
        val type: Int,
        var sender_id: Long? = null,
        var sender_name: String? = null,
        var date: Long? = null,
        var is_send: Boolean? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readValue(Long::class.java.classLoader) as? Long,
            parcel.readString(),
            parcel.readValue(Long::class.java.classLoader) as? Long,
            parcel.readValue(Boolean::class.java.classLoader) as? Boolean) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(source)
        parcel.writeInt(type)
        parcel.writeValue(sender_id)
        parcel.writeString(sender_name)
        parcel.writeValue(date)
        parcel.writeValue(is_send)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MMData> {
        override fun createFromParcel(parcel: Parcel): MMData {
            return MMData(parcel)
        }

        override fun newArray(size: Int): Array<MMData?> {
            return arrayOfNulls(size)
        }
    }

}