package com.buur.frederik.multimediechatexample.api

import com.buur.frederik.multimediechatexample.models.UploadResponse
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface IUpload {

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

}