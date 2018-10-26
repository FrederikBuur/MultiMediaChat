package com.buur.frederik.multimediechat.models.gif

import android.os.Parcel
import android.os.Parcelable

class ImagesData(
        val fixed_width_still: Gif,
        val fixed_height_still: Gif,
        val fixed_width: Gif,
        val fixed_height: Gif,
        val original: Gif,
        val preview_gif: Gif
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readParcelable(Gif::class.java.classLoader),
            parcel.readParcelable(Gif::class.java.classLoader),
            parcel.readParcelable(Gif::class.java.classLoader),
            parcel.readParcelable(Gif::class.java.classLoader),
            parcel.readParcelable(Gif::class.java.classLoader),
            parcel.readParcelable(Gif::class.java.classLoader)) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(fixed_width_still, flags)
        parcel.writeParcelable(fixed_height_still, flags)
        parcel.writeParcelable(fixed_width, flags)
        parcel.writeParcelable(fixed_height, flags)
        parcel.writeParcelable(original, flags)
        parcel.writeParcelable(preview_gif, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ImagesData> {
        override fun createFromParcel(parcel: Parcel): ImagesData {
            return ImagesData(parcel)
        }

        override fun newArray(size: Int): Array<ImagesData?> {
            return arrayOfNulls(size)
        }
    }
}