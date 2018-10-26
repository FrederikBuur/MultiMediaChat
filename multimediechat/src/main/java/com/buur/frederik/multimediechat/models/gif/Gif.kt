package com.buur.frederik.multimediechat.models.gif

import android.os.Parcel
import android.os.Parcelable

class Gif(
        val url: String,
        val width: String,
        val height: String,
        val size: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(url)
        parcel.writeString(width)
        parcel.writeString(height)
        parcel.writeString(size)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Gif> {
        override fun createFromParcel(parcel: Parcel): Gif {
            return Gif(parcel)
        }

        override fun newArray(size: Int): Array<Gif?> {
            return arrayOfNulls(size)
        }
    }
}