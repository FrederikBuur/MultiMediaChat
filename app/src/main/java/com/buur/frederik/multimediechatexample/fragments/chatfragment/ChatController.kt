package com.buur.frederik.multimediechatexample.fragments.chatfragment

import android.content.Context
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.buur.frederik.multimediechat.enums.MMDataType
import com.buur.frederik.multimediechat.helpers.ImageHelper
import com.buur.frederik.multimediechat.models.MMData
import com.buur.frederik.multimediechatexample.api.IUpload
import com.buur.frederik.multimediechatexample.controllers.MultiMediaApplication
import com.buur.frederik.multimediechatexample.controllers.ServiceGenerator
import com.google.gson.Gson
import com.trello.rxlifecycle2.components.support.RxFragment
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import io.socket.client.Socket
import io.socket.emitter.Emitter
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

class ChatController {

    private val tag = "ChatController"

    private var act: AppCompatActivity? = null
    private var socket: Socket? = null
    private var publishSubjectDisposable: Disposable? = null
    private var publishSubject: PublishSubject<MMData>? = null

    private var uploadAPI: IUpload? = null

    private fun getUploadClient(): IUpload {
        if (uploadAPI == null) {
            uploadAPI = ServiceGenerator().createUploadAPI()
        }
        return uploadAPI!!
    }

    init {
        this.publishSubject = PublishSubject.create()
    }

    private val onNewMessage = Emitter.Listener { args ->
        this.act?.runOnUiThread(Runnable {
            try {
                val mmData = Gson().fromJson(args[0].toString(), MMData::class.java)
                publishSubject?.onNext(mmData)
            } catch (e: JSONException) {
                e.message
                return@Runnable
            }
        })
    }

    fun establishServerConnection(context: Context?) {
        this.act = context as? AppCompatActivity
        this.act?.let {
            this.socket = (it.application as? MultiMediaApplication)?.socket
            this.socket?.on(TOPIC_NEW_MESSAGE, onNewMessage)
            this.socket?.connect()
        }
    }

    fun newMessagesPublisher(): Observable<MMData>? {
        return this.publishSubject
                ?.doOnSubscribe {
                    this.publishSubjectDisposable = it
                }
                ?.doOnNext {
                    Log.d(TAG, "New message type: ${it.type}, source: ${it.source}")
                }
    }

    fun sendMessageToServer(mmData: MMData): Observable<*> {

        return if (socket?.connected() == true) {
            when (mmData.type) {
                MMDataType.Text.ordinal, MMDataType.Gif.ordinal -> {
                    val gson = Gson().toJson(mmData)
                    Observable.just(socket?.emit(TOPIC_NEW_MESSAGE, gson))
                }
                MMDataType.Image.ordinal -> {
                    ImageHelper.prepareImagePathToUpload(mmData)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .concatMap { body ->
                                getUploadClient().postImage(body)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                            }
                            .doOnNext { uploadResponse ->
                                mmData.source = uploadResponse.url
                                val gson = Gson().toJson(mmData)
                                socket?.emit(TOPIC_NEW_MESSAGE, gson)
                            }
                }
                MMDataType.File.ordinal -> {
                    Observable.just("TODO")
                }
                MMDataType.Video.ordinal -> {
                    Observable.just("TODO")
                }
                MMDataType.Audio.ordinal -> {
                    Observable.just("TODO")
                }
                else -> {
                    Observable.just(Log.e(tag, "trying to send unknown mmdata type")) // other type then expected
                }

            }
        } else {
            Observable.just("Socket lost connection")
        }
    }

    companion object {
        const val TAG = "ChatController"
        const val TOPIC_NEW_MESSAGE = "new_message"
    }

}