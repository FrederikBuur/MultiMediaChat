package com.buur.frederik.multimediechatexample.controllers

import com.buur.frederik.multimediechatexample.api.IMultiMedia
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class ServiceGenerator {

    private var retrofit: Retrofit? = null

    private fun getRetrofit(): Retrofit {
        val log = HttpLoggingInterceptor()
        log.level = HttpLoggingInterceptor.Level.BODY
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                    .baseUrl(MM_BASE_URL)
                    .client(OkHttpClient.Builder().addInterceptor(log).build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()
        }
        return  retrofit!!
    }

    fun createMultiMediaAPI() : IMultiMedia {
        return getRetrofit().create(IMultiMedia::class.java)
    }

    companion object {
//        const val MM_BASE_URL = "https://frozen-stream-46849.herokuapp.com/" // heroku
        const val MM_BASE_URL = "http://192.168.1.13:8000/" // home
        //    const val MM_BASE_URL = "http://192.168.1.239:3000/api/" // amsiq
    }

}