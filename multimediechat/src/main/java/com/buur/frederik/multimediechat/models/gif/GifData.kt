package com.buur.frederik.multimediechat.models.gif

import android.os.Parcel
import android.os.Parcelable

class GifData(
        val type: String,
        val id: String,
        val url: String,
        val embed_url: String,
        val images: ImagesData
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readParcelable(ImagesData::class.java.classLoader)) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(type)
        parcel.writeString(id)
        parcel.writeString(url)
        parcel.writeString(embed_url)
        parcel.writeParcelable(images, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GifData> {
        override fun createFromParcel(parcel: Parcel): GifData {
            return GifData(parcel)
        }

        override fun newArray(size: Int): Array<GifData?> {
            return arrayOfNulls(size)
        }
    }
}