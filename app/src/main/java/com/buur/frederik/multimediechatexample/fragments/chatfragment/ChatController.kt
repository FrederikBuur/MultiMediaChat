package com.buur.frederik.multimediechatexample.fragments.chatfragment

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.buur.frederik.multimediechat.enums.MMDataType
import com.buur.frederik.multimediechat.helpers.UploadHelper
import com.buur.frederik.multimediechat.models.MMData
import com.buur.frederik.multimediechatexample.activities.MainActivity
import com.buur.frederik.multimediechatexample.api.IUpload
import com.buur.frederik.multimediechatexample.controllers.MultiMediaApplication
import com.buur.frederik.multimediechatexample.controllers.ServiceGenerator
import com.buur.frederik.multimediechatexample.controllers.SessionController
import com.buur.frederik.multimediechatexample.models.NewEventResponse
import com.buur.frederik.multimediechatexample.models.User
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONException
import java.util.concurrent.TimeUnit

class ChatController {

    private val tag = "ChatController"
    private val typingDebounceDuration = 2000L

    private var act: AppCompatActivity? = null
    private var socket: Socket? = null
    private var eventPublishSubject: PublishSubject<NewEventResponse>? = null
    private var typingPublishSubject: PublishSubject<Int>? = null
    private var typingDisposable: Disposable? = null

    private var context: Context? = null
    private var uploadAPI: IUpload? = null
    private var userIsTyping: Boolean? = null

    private fun getUploadClient(): IUpload {
        if (uploadAPI == null) {
            uploadAPI = ServiceGenerator().createUploadAPI()
        }
        return uploadAPI!!
    }

    init {
        this.eventPublishSubject = PublishSubject.create()
        this.typingPublishSubject = PublishSubject.create()
    }

    private val onNewMessage = Emitter.Listener { args ->
        this.act?.runOnUiThread(Runnable {
            try {
                val mmData = Gson().fromJson(args[0].toString(), MMData::class.java)
                val event = NewEventResponse(EventType.Message.ordinal, mmData)
                eventPublishSubject?.onNext(event)
            } catch (e: JSONException) {
                e.message
                return@Runnable
            }
        })
    }

    private val onUserJoined = Emitter.Listener { args ->
        this.act?.runOnUiThread(Runnable {
            try {
                val userName = args[0].toString()
                val event = NewEventResponse(EventType.UserConnected.ordinal, userName)
                eventPublishSubject?.onNext(event)
            } catch (e: JSONException) {
                e.message
                return@Runnable
            }
        })
    }

    private val onUserTyping = Emitter.Listener { args ->
        this.act?.runOnUiThread(Runnable {
            try {
                val user = Gson().fromJson(args[0].toString(), User::class.java)
                val event = NewEventResponse(EventType.StartTyping.ordinal, user)
                eventPublishSubject?.onNext(event)
            } catch (e: JSONException) {
                e.message
                return@Runnable
            }
        })
    }

    private val onUserStopTyping = Emitter.Listener { args ->
        this.act?.runOnUiThread(Runnable {
            try {
                val user = Gson().fromJson(args[0].toString(), User::class.java)
                val event = NewEventResponse(EventType.StopTyping.ordinal, user)
                eventPublishSubject?.onNext(event)
            } catch (e: JSONException) {
                e.message
                return@Runnable
            }
        })
    }

    fun startServerConnection(context: Context?) {
        this.context = context
        this.act = context as? AppCompatActivity
        this.act?.let {
            this.socket = (it.application as? MultiMediaApplication)?.socket
            this.socket?.on(TOPIC_NEW_MESSAGE, onNewMessage)
            this.socket?.on(TOPIC_USER_JOINED, onUserJoined)
            this.socket?.on(TOPIC_USER_TYPING, onUserTyping)
            this.socket?.on(TOPIC_USER_STOP_TYPING, onUserStopTyping)
            this.socket?.connect()

            // emit that new user joined chat
            SessionController.getInstance().getUser()?.name?.let { name ->
                this.socket?.emit(TOPIC_USER_JOINED, name)
                setupTypingPublisher()
            }
        }
    }

    fun stopServerConnection() {
        this.socket?.disconnect()
        this.socket?.off(TOPIC_NEW_MESSAGE, onNewMessage)
        this.socket?.off(TOPIC_USER_JOINED, onUserJoined)
        this.socket?.off(TOPIC_USER_TYPING, onUserTyping)
        this.socket?.off(TOPIC_USER_STOP_TYPING, onUserStopTyping)
    }

    fun newMessagesPublisher(): Observable<NewEventResponse>? {
        return this.eventPublishSubject
    }

    fun sendMessageToServer(mmData: MMData): Observable<*> {
        return if (socket?.connected() == true) {
            when (mmData.type) {
                MMDataType.Text.ordinal, MMDataType.Gif.ordinal -> {
                    userStartedTyping(false)
                    val gson = Gson().toJson(mmData)
                    Observable.just(socket?.emit(TOPIC_NEW_MESSAGE, gson))
                }
                MMDataType.Image.ordinal,
                MMDataType.Audio.ordinal,
                MMDataType.Document.ordinal,
                MMDataType.Video.ordinal -> {
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
                                userStartedTyping(false)
                                mmData.source = uploadResponse.url
                                val gson = Gson().toJson(mmData)
                                Observable.just(socket?.emit(TOPIC_NEW_MESSAGE, gson))
                            }
                            .doOnError {
                                it
                            }
                }
                else -> {
                    Observable.just(Log.e(tag, "trying to send unknown mmdata type")) // other type then expected
                }
            }

        } else {
            Observable.just("No connection to server")
        }
    }

    private fun setupTypingPublisher() {
        val disp = this.typingPublishSubject
                //?.compose(currentFragment.bindToLifecycle())
                ?.debounce(typingDebounceDuration, TimeUnit.MILLISECONDS)
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.doOnSubscribe {
                    this.typingDisposable = it
                }
                ?.doOnNext {
                    userIsTyping = false
                    userStartedTyping(false)
                }
                ?.doOnError {
                    it
                }?.subscribe()
    }

    fun userStartedTyping(didStartTyping: Boolean) {
        SessionController.getInstance().getUser()?.let { user ->
            val gson = Gson().toJson(user)
            if (didStartTyping) {
                typingPublishSubject?.onNext(1)
                if (userIsTyping != true) {
                    socket?.emit(TOPIC_USER_TYPING, gson)
                }
                userIsTyping = true
            } else if (userIsTyping == false) {
                socket?.emit(TOPIC_USER_STOP_TYPING, gson)
                userIsTyping = false
            }
        }
    }

    companion object {
        const val TOPIC_NEW_MESSAGE = "new_message"
        const val TOPIC_USER_JOINED = "user_joined"
        const val TOPIC_USER_TYPING = "user_typing"
        const val TOPIC_USER_STOP_TYPING = "user_stop_typing"
    }
}