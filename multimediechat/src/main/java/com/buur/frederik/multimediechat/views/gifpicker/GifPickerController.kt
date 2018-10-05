package com.buur.frederik.multimediechat.views.gifpicker

import android.util.Log
import com.buur.frederik.multimediechat.api.ServiceGenerator
import com.buur.frederik.multimediechat.models.gif.GifMultipleGifResponse
import io.reactivex.Observable

class GifPickerController {

    private val GIPHY_API_KEY = "Ms6Tzk690HOJO8FU52fPndoLlVBlR8W6"
    private var gifClient: IGiphy? = null

    private fun getGifClient(): IGiphy {
        if (gifClient == null) {
            gifClient = ServiceGenerator().createGiphyAPI()
        }
        return gifClient!!
    }

    fun getTrendingGifs(): Observable<GifMultipleGifResponse>? {
        return getGifClient().getTrendingGifs(GIPHY_API_KEY, 30, "G")
                .doOnNext {
                }
                .doOnError { error ->
                    Log.d(this.toString(), error.message)
                }
    }

    fun getSearchGifs(search: String): Observable<GifMultipleGifResponse> {
        return getGifClient().getSearchGifs(GIPHY_API_KEY, search, 30, 0, "G", "en")
                .doOnNext {
                }
                .doOnError { error ->
                    Log.d(this.toString(), error.message)
                }
    }

}