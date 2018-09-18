package com.buur.frederik.multimediechatexample.dummybackend

import com.buur.frederik.multimediechat.enums.MMDataType
import com.buur.frederik.multimediechat.models.MMData

class SampleData {

    companion object {

        val dummyData = arrayListOf(
                MMData("First text message", MMDataType.Text.ordinal),
                MMData("Long test message Long test message Long test message Long test message " +
                        "Long test message Long test message Long test message Long test message Long test message " +
                        "Long test message Long test message Long test message Long test message Long test message " +
                        "Long test message Long test message Long test message Long test message ", MMDataType.Text.ordinal),
                MMData("ImageBitmap", MMDataType.Image.ordinal),
                MMData("ImageBitmap", MMDataType.Image.ordinal),
                MMData("GifSource", MMDataType.Gif.ordinal),
                MMData("GifSource", MMDataType.Gif.ordinal),
                MMData("VideoBitmap", MMDataType.Video.ordinal),
                MMData("VideoBitmap", MMDataType.Video.ordinal),
                MMData("AudioBitmap", MMDataType.Audio.ordinal),
                MMData("AudioBitmap", MMDataType.Audio.ordinal)
                )

    }

}