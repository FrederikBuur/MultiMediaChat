package com.buur.frederik.multimediechat.api

import com.buur.frederik.multimediechat.views.gifpicker.IGiphy
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class MMServiceGenerator {

    companion object {
        const val API_KEY = "Z7vOeiwK7oOmm6gcD5vveB373l3cyhrG"
        const val GIPHY_BASE_URL = "https://api.giphy.com"
    }

    private var retrofit: Retrofit? = null

    private fun getRetrofit(): Retrofit {
        val log = HttpLoggingInterceptor()
        log.level = HttpLoggingInterceptor.Level.BODY
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                    .baseUrl(GIPHY_BASE_URL)
                    .client(OkHttpClient.Builder().addInterceptor(log).build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()
        }
        return  retrofit!!
    }

    fun createGiphyAPI() : IGiphy {
        return getRetrofit().create(IGiphy::class.java)
    }


}