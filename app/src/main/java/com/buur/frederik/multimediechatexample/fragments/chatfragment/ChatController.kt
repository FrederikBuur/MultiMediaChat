package com.buur.frederik.multimediechatexample.fragments.chatfragment

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.buur.frederik.multimediechat.enums.MMDataType
import com.buur.frederik.multimediechat.helpers.UploadHelper
import com.buur.frederik.multimediechat.models.MMData
import com.buur.frederik.multimediechatexample.api.IMultiMedia
import com.buur.frederik.multimediechatexample.controllers.MultiMediaApplication
import com.buur.frederik.multimediechatexample.controllers.ServiceGenerator
import com.buur.frederik.multimediechatexample.controllers.SessionController
import com.buur.frederik.multimediechatexample.models.NewEventResponse
import com.buur.frederik.multimediechatexample.models.User
import com.google.gson.Gson
import com.trello.rxlifecycle3.components.support.RxFragment
import com.trello.rxlifecycle3.kotlin.bindToLifecycle
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

class ChatController {

    private val typingDebounceDuration = 2000L

    private var act: AppCompatActivity? = null
    private var socket: Socket? = null
    var eventPublishSubject: PublishSubject<NewEventResponse>? = null
    private var typingPublishSubject: PublishSubject<Int>? = null
    private var typingDisposable: Disposable? = null

    private var multiMediaAPI: IMultiMedia? = null
    private var userIsTyping: Boolean? = null

    private fun getMultiMediaClient(): IMultiMedia {
        if (multiMediaAPI == null) {
            multiMediaAPI = ServiceGenerator().createMultiMediaAPI()
        }
        return multiMediaAPI!!
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

    fun startServerListeners(context: Context?) {
        this.act = context as? AppCompatActivity
        this.act?.let {
            this.socket = (it.application as? MultiMediaApplication)?.socket
            this.socket?.on(TOPIC_NEW_MESSAGE, onNewMessage)
            this.socket?.on(TOPIC_USER_JOINED, onUserJoined)
            this.socket?.on(TOPIC_USER_TYPING, onUserTyping)
            this.socket?.on(TOPIC_USER_STOP_TYPING, onUserStopTyping)
            this.socket?.connect()

            setupTypingPublisher()
            emitUserIsConnected(this.socket)
        }
    }

    fun stopServerConnection() {
        this.socket?.disconnect()
        this.socket?.off(TOPIC_NEW_MESSAGE, onNewMessage)
        this.socket?.off(TOPIC_USER_JOINED, onUserJoined)
        this.socket?.off(TOPIC_USER_TYPING, onUserTyping)
        this.socket?.off(TOPIC_USER_STOP_TYPING, onUserStopTyping)
    }

    fun sendMessageToServer(mmData: MMData): Observable<Int> {
        return Observable.create { emitter ->
            if (socket?.connected() == true) {
                when (mmData.type) {
                    MMDataType.Text.ordinal, MMDataType.Gif.ordinal -> {
                        userIsTyping = false
                        userStartedTyping(false)
                        val gson = Gson().toJson(mmData)
                        socket?.emit(TOPIC_NEW_MESSAGE, gson)
                        emitter.onNext(1)
                        emitter.onComplete()
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
                                        MMDataType.Image.ordinal -> getMultiMediaClient().postImage(body)
                                        MMDataType.Audio.ordinal -> getMultiMediaClient().postAudio(body)
                                        MMDataType.Video.ordinal -> getMultiMediaClient().postVideo(body)
                                        MMDataType.Document.ordinal -> getMultiMediaClient().postDocument(body)
                                        else -> null
                                    }
                                    uploadObservable
                                            ?.subscribeOn(Schedulers.io())
                                            ?.observeOn(AndroidSchedulers.mainThread())
                                }
                                .doOnNext { uploadResponse ->
                                    if (mmData.type == MMDataType.Audio.ordinal) {
                                        MMData.deleteFile(mmData.source)
                                    }
                                    mmData.source = uploadResponse.url
                                    val gson = Gson().toJson(mmData)
                                    socket?.emit(TOPIC_NEW_MESSAGE, gson)
                                    emitter.onNext(1)
                                    emitter.onComplete()
                                }
                                .doOnError { error ->
                                    when (error) {
                                        is SocketException, is SocketTimeoutException -> {
                                            if (mmData.type == MMDataType.Audio.ordinal) {
                                                MMData.deleteFile(mmData.source)
                                            }
                                        }
                                        else -> {
                                            error
                                        }
                                    }
                                    emitter.onError(error)
                                    emitter.onComplete()
                                }
                                .subscribe({}, {})
                    }
                    else -> {
                        emitter.onError(Throwable("Unknown MMData type"))
                        emitter.onComplete()
                    }
                }

            } else {
                if (mmData.type == MMDataType.Audio.ordinal) {
                    MMData.deleteFile(mmData.source)
                }
                emitter.onError(SocketException("No connection to server"))
                emitter.onComplete()
            }
        }
    }

    fun getLatestMessages(fragment: RxFragment): Observable<ArrayList<MMData>> {
        return getMultiMediaClient().getLatestMessages()
                .bindToLifecycle(fragment)
                .doOnError { error ->
                    error.printStackTrace()
                }

    }

    private fun setupTypingPublisher() {
        this.typingPublishSubject
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
        if (socket?.connected() != true) return

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

        fun emitUserIsConnected(socket: Socket?) {
            // emit that new user joined chat
            SessionController.getInstance().getUser()?.name?.let { name ->
                socket?.emit(TOPIC_USER_JOINED, name)
            }
        }

    }
}