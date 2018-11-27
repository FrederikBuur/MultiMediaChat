package com.buur.frederik.multimediechatexample.api

import com.buur.frederik.multimediechat.models.MMData
import com.buur.frederik.multimediechatexample.models.LatestMessagesResponse
import com.buur.frederik.multimediechatexample.models.UploadResponse
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface IMultiMedia {

    @GET("latest-messages/")
    fun getLatestMessages(): Observable<ArrayList<MMData>>

    @Multipart
    @POST("image/")
    fun postImage(
            @Part image: MultipartBody.Part
    ): Observable<UploadResponse>

    @Multipart
    @POST("audio/")
    fun postAudio(
            @Part audio: MultipartBody.Part
    ): Observable<UploadResponse>

    @Multipart
    @POST("video/")
    fun postVideo(
            @Part video: MultipartBody.Part
    ): Observable<UploadResponse>

    @Multipart
    @POST("document/")
    fun postDocument(
            @Part document: MultipartBody.Part
    ): Observable<UploadResponse>

}