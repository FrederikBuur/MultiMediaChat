package com.buur.frederik.multimediechat.gifpicker

import com.buur.frederik.multimediechat.models.gif.GifMultipleGifResponse
import com.buur.frederik.multimediechat.models.gif.GifRandomResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface IGiphy {

    @GET("/v1/gifs/random")
    fun getRandomGif(
            @Query("api_key") api_key: String,
            @Query("tag") tag: String,
            @Query("rating") rating: String
    ): Observable<GifRandomResponse>

    @GET("https://api.giphy.com/v1/gifs/trending")
    fun getTrendingGifs(
            @Query("api_key") api_key: String,
            @Query("limit") limit: Int,
            @Query("rating") rating: String
    ): Observable<GifMultipleGifResponse>

    @GET("https://api.giphy.com/v1/gifs/search")
    fun getSearchGifs(
            @Query("api_key") api_key: String,
            @Query("q") search: String,
            @Query("limit") limit: Int,
            @Query("offset") offset: Int,
            @Query("rating") rating: String,
            @Query("lang") lang: String
    ): Observable<GifMultipleGifResponse>

}