package com.buur.frederik.multimediechatexample.dummybackend

import com.buur.frederik.multimediechat.enums.MMDataType
import com.buur.frederik.multimediechat.models.MMData

class SampleData {

    companion object {

        var dummyData: ArrayList<MMData>? = null

        fun populateDummyData(): ArrayList<MMData> {
            val data = arrayListOf(
                    MMData(1, "First text message", MMDataType.Text.ordinal),
                    MMData(2, "Long test message Long test message Long test message Long test message " +
                            "Long test message Long test message Long test message Long test message Long test message " +
                            "Long test message Long test message Long test message Long test message Long test message " +
                            "Long test message Long test message Long test message Long test message ", MMDataType.Text.ordinal),
                    MMData(3, "https://cloud.netlifyusercontent.com/assets/344dbf88-fdf9-42bb-adb4-46f01eedd629/242ce817-97a3-48fe-9acd-b1bf97930b01/09-posterization-opt.jpg", MMDataType.Image.ordinal),
                    MMData(4, "https://wallpaperbrowse.com/media/images/3848765-wallpaper-images-download.jpg", MMDataType.Image.ordinal),
//                    MMData("GifSource", MMDataType.Gif.ordinal),
//                    MMData("GifSource", MMDataType.Gif.ordinal),
//                    MMData("VideoBitmap", MMDataType.Video.ordinal),
//                    MMData("VideoBitmap", MMDataType.Video.ordinal),
//                    MMData("AudioBitmap", MMDataType.Audio.ordinal),
//                    MMData("AudioBitmap", MMDataType.Audio.ordinal),
                    MMData(5, "First text message", MMDataType.Text.ordinal),
                    MMData(6, "Long test message Long test message Long test message Long test message " +
                            "Long test message Long test message Long test message Long test message Long test message " +
                            "Long test message Long test message Long test message Long test message Long test message " +
                            "Long test message Long test message Long test message Long test message ", MMDataType.Text.ordinal),
                    MMData(7, "https://cloud.netlifyusercontent.com/assets/344dbf88-fdf9-42bb-adb4-46f01eedd629/242ce817-97a3-48fe-9acd-b1bf97930b01/09-posterization-opt.jpg", MMDataType.Image.ordinal),
                    MMData(12, "https://wallpaperbrowse.com/media/images/3848765-wallpaper-images-download.jpg", MMDataType.Image.ordinal),
//                    MMData("VideoBitmap", MMDataType.Video.ordinal),
//                    MMData("VideoBitmap", MMDataType.Video.ordinal),
//                    MMData("AudioBitmap", MMDataType.Audio.ordinal),
//                    MMData("AudioBitmap", MMDataType.Audio.ordinal),
                    MMData(8, "First text message", MMDataType.Text.ordinal),
                    MMData(9, "Long test message Long test message Long test message Long test message " +
                            "Long test message Long test message Long test message Long test message Long test message " +
                            "Long test message Long test message Long test message Long test message Long test message " +
                            "Long test message Long test message Long test message Long test message ", MMDataType.Text.ordinal),
                    MMData(13, "https://media.giphy.com/media/3ohjV3cQ9lvPeCVLOg/giphy.gif", MMDataType.Gif.ordinal),
                    MMData(14, "https://user-images.githubusercontent.com/512439/32188373-da40378e-bd64-11e7-88f7-b6c29b81760d.gif", MMDataType.Gif.ordinal),
                    MMData(10, "https://cloud.netlifyusercontent.com/assets/344dbf88-fdf9-42bb-adb4-46f01eedd629/242ce817-97a3-48fe-9acd-b1bf97930b01/09-posterization-opt.jpg", MMDataType.Image.ordinal),
                    MMData(11, "https://wallpaperbrowse.com/media/images/3848765-wallpaper-images-download.jpg", MMDataType.Image.ordinal),
//                    MMData("GifSource", MMDataType.Gif.ordinal),
//                    MMData("GifSource", MMDataType.Gif.ordinal),
                    MMData(15, "https://www.demonuts.com/Demonuts/smallvideo.mp4", MMDataType.Video.ordinal)
//                    MMData("VideoBitmap", MMDataType.Video.ordinal),
//                    MMData("AudioBitmap", MMDataType.Audio.ordinal),
//                    MMData("AudioBitmap", MMDataType.Audio.ordinal)
            )
            dummyData = data
            return data
        }


    }

}