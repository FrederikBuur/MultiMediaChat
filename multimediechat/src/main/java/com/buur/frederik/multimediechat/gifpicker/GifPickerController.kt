package com.buur.frederik.multimediechat.gifpicker

import android.util.Log
import com.buur.frederik.multimediechat.models.gif.GifMultipleGifResponse
import io.reactivex.Observable

class GifPickerController {

    private val tag = "GifPickerController"
    private var gifClient: IGiphy? = null

    private fun getGifClient(): IGiphy {
        if (gifClient == null) {
            gifClient = MMServiceGenerator().createGiphyAPI()
        }
        return gifClient!!
    }

    fun getTrendingGifs(offset: Int): Observable<GifMultipleGifResponse> {
        return getGifClient().getTrendingGifs(GIPHY_API_KEY, gifFetchAmount, offset, "G")
                .doOnNext {
                }
                .doOnError { error ->
                    Log.d(tag, error.message)
                }
    }

    fun getSearchGifs(search: String, offset: Int): Observable<GifMultipleGifResponse> {
        return getGifClient().getSearchGifs(GIPHY_API_KEY, search, gifFetchAmount, offset, "G", "en")
                .doOnNext {
                }
                .doOnError { error ->
                    Log.d(tag, error.message)
                }
    }

    companion object {
        const val gifFetchAmount = 30
        const val GIPHY_API_KEY = "Ms6Tzk690HOJO8FU52fPndoLlVBlR8W6"
    }

}