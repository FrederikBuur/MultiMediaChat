package com.buur.frederik.multimediechatexample.fragments.chatfragment

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.buur.frederik.multimediechat.enums.MMDataType
import com.buur.frederik.multimediechat.helpers.UploadHelper
import com.buur.frederik.multimediechat.models.MMData
import com.buur.frederik.multimediechatexample.api.IUpload
import com.buur.frederik.multimediechatexample.controllers.MultiMediaApplication
import com.buur.frederik.multimediechatexample.controllers.ServiceGenerator
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONException

class ChatController {

    private val tag = "ChatController"

    private var act: AppCompatActivity? = null
    private var socket: Socket? = null
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
    }

    fun sendMessageToServer(mmData: MMData): Observable<*> {
        return if (socket?.connected() == true) {
            when (mmData.type) {
                MMDataType.Text.ordinal, MMDataType.Gif.ordinal -> {
                    val gson = Gson().toJson(mmData)
                    Observable.just(socket?.emit(TOPIC_NEW_MESSAGE, gson))
                }
                MMDataType.Image.ordinal,
                MMDataType.Audio.ordinal,
                MMDataType.Document.ordinal,
                MMDataType.Video.ordinal-> {
                    UploadHelper.prepareMMDataToUpload(mmData)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .concatMap { body ->
                                val uploadObservable = when (mmData.type) {
                                    MMDataType.Image.ordinal -> getUploadClient().postImage(body)
                                    MMDataType.Audio.ordinal -> getUploadClient().postAudio(body)
                                    MMDataType.Video.ordinal -> getUploadClient().postVideo(body)
                                    MMDataType.Document.ordinal -> getUploadClient().postDocument(body)
                                    else -> null
                                }
                                uploadObservable
                                        ?.subscribeOn(Schedulers.io())
                                        ?.observeOn(AndroidSchedulers.mainThread())
                            }
                            .doOnNext { uploadResponse ->
                                mmData.source = uploadResponse.url
                                val gson = Gson().toJson(mmData)
                                Observable.just(socket?.emit(TOPIC_NEW_MESSAGE, gson))
                            }
                            .doOnError {
                                it
                            }
                }
                MMDataType.Document.ordinal -> {
                    Observable.just("TODO")
                }
                else -> {
                    Observable.just(Log.e(tag, "trying to send unknown mmdata type")) // other type then expected
                }

            }
        } else {
            Observable.just("No connection to server")
        }
    }

    companion object {
        const val TOPIC_NEW_MESSAGE = "new_message"
    }

}