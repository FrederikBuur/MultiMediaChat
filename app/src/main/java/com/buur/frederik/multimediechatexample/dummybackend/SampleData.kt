package com.buur.frederik.multimediechatexample.dummybackend

import com.buur.frederik.multimediechat.enums.MMDataType
import com.buur.frederik.multimediechat.models.MMData

class SampleData {

    companion object {

        var dummyData: ArrayList<MMData>? = null

        fun populateDummyData(): ArrayList<MMData> {
            val data = arrayListOf(
                    MMData("First text message", MMDataType.Text.ordinal),
                    MMData("Long test message Long test message Long test message Long test message " +
                            "Long test message Long test message Long test message Long test message Long test message " +
                            "Long test message Long test message Long test message Long test message Long test message " +
                            "Long test message Long test message Long test message Long test message ", MMDataType.Text.ordinal),
                    MMData("https://cloud.netlifyusercontent.com/assets/344dbf88-fdf9-42bb-adb4-46f01eedd629/242ce817-97a3-48fe-9acd-b1bf97930b01/09-posterization-opt.jpg", MMDataType.Image.ordinal),
                    MMData("https://wallpaperbrowse.com/media/images/3848765-wallpaper-images-download.jpg", MMDataType.Image.ordinal),
//                    MMData("GifSource", MMDataType.Gif.ordinal),
//                    MMData("GifSource", MMDataType.Gif.ordinal),
//                    MMData("VideoBitmap", MMDataType.Video.ordinal),
//                    MMData("VideoBitmap", MMDataType.Video.ordinal),
//                    MMData("AudioBitmap", MMDataType.Audio.ordinal),
//                    MMData("AudioBitmap", MMDataType.Audio.ordinal),
                    MMData("First text message", MMDataType.Text.ordinal),
                    MMData("Long test message Long test message Long test message Long test message " +
                            "Long test message Long test message Long test message Long test message Long test message " +
                            "Long test message Long test message Long test message Long test message Long test message " +
                            "Long test message Long test message Long test message Long test message ", MMDataType.Text.ordinal),
                    MMData("https://cloud.netlifyusercontent.com/assets/344dbf88-fdf9-42bb-adb4-46f01eedd629/242ce817-97a3-48fe-9acd-b1bf97930b01/09-posterization-opt.jpg", MMDataType.Image.ordinal),
                    MMData("https://wallpaperbrowse.com/media/images/3848765-wallpaper-images-download.jpg", MMDataType.Image.ordinal),
//                    MMData("GifSource", MMDataType.Gif.ordinal),
//                    MMData("GifSource", MMDataType.Gif.ordinal),
//                    MMData("VideoBitmap", MMDataType.Video.ordinal),
//                    MMData("VideoBitmap", MMDataType.Video.ordinal),
//                    MMData("AudioBitmap", MMDataType.Audio.ordinal),
//                    MMData("AudioBitmap", MMDataType.Audio.ordinal),
                    MMData("First text message", MMDataType.Text.ordinal),
                    MMData("Long test message Long test message Long test message Long test message " +
                            "Long test message Long test message Long test message Long test message Long test message " +
                            "Long test message Long test message Long test message Long test message Long test message " +
                            "Long test message Long test message Long test message Long test message ", MMDataType.Text.ordinal),
                    MMData("https://cloud.netlifyusercontent.com/assets/344dbf88-fdf9-42bb-adb4-46f01eedd629/242ce817-97a3-48fe-9acd-b1bf97930b01/09-posterization-opt.jpg", MMDataType.Image.ordinal),
                    MMData("https://wallpaperbrowse.com/media/images/3848765-wallpaper-images-download.jpg", MMDataType.Image.ordinal),
//                    MMData("GifSource", MMDataType.Gif.ordinal),
//                    MMData("GifSource", MMDataType.Gif.ordinal),
                    MMData("https://www.demonuts.com/Demonuts/smallvideo.mp4", MMDataType.Video.ordinal)
//                    MMData("VideoBitmap", MMDataType.Video.ordinal),
//                    MMData("AudioBitmap", MMDataType.Audio.ordinal),
//                    MMData("AudioBitmap", MMDataType.Audio.ordinal)
            )
            dummyData = data
            return data
        }


    }

}