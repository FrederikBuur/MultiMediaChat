package com.buur.frederik.multimediechat.models

import android.os.Parcel
import android.os.Parcelable

class MMData(
        var id: Long,
        var source: String,
        val type: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readString(),
            parcel.readInt()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(source)
        parcel.writeInt(type)
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