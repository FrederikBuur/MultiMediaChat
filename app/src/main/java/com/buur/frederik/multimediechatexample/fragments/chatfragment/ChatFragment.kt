package com.buur.frederik.multimediechatexample.fragments.chatfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.buur.frederik.multimediechat.helpers.MMToast
import com.buur.frederik.multimediechat.inputfield.ISendMessage
import com.buur.frederik.multimediechat.inputfield.MMInputFragment
import com.buur.frederik.multimediechat.models.MMData
import com.buur.frederik.multimediechatexample.R
import com.buur.frederik.multimediechatexample.controllers.SessionController
import com.buur.frederik.multimediechatexample.fragments.MMFragment
import com.buur.frederik.multimediechatexample.fragments.loginfragment.LoginFragment
import com.buur.frederik.multimediechatexample.models.User
import com.jakewharton.rxbinding2.view.layoutChangeEvents
import com.jakewharton.rxbinding2.widget.textChanges
import com.trello.rxlifecycle3.kotlin.bindToLifecycle
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_chat.*

class ChatFragment : MMFragment(), ISendMessage {

    private var adapter: ChatAdapter? = null
    private var messageList: ArrayList<MMData>? = null

    private var sharedVisibleItemCount = 0

    private var mmInputFrag: MMInputFragment? = null

    private var chatController: ChatController? = null
    private var newMessageDisposable: Disposable? = null
    private var isTypingDisposable: Disposable? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

            shouldShowLoginPage()

        setupViews()
    }

    private fun setupViews() {

        if (chatController == null) {
            chatController = ChatController()
        }
        if (messageList == null) {
            messageList = ArrayList()
            fetchLatestMessages()
        }
        chatController?.startServerListeners(context)

        setupRecyclerView()
        setupMMLib()

        scrollToBottomPost()
        setupNewEventListener()
        setupTypingListener()
        setupResizeListener()
    }

    private fun setupResizeListener() {
        chatRecyclerView.layoutChangeEvents()
                .bindToLifecycle(this)
                .doOnNext {
                    if (it.bottom() < it.oldBottom()) {
                        scrollToBottomIfNearBottom()
                    }
                }
                .subscribe({}, {})
    }

    private fun setupNewEventListener() {
        newMessageDisposable = chatController?.eventPublishSubject
                ?.compose(bindToLifecycle())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe({ event ->
                    val type = event.type
                    when (type) {
                        EventType.Message.ordinal -> {
                            val mmData = event.data as MMData
                            messageList?.add(mmData)
                            adapter?.notifyDataSetChanged()
                            scrollToBottomIfNearBottom(true)
                        }
                        EventType.UserConnected.ordinal -> {
                            val name = event.data as String
                            this.mainActivity?.showTopNotification("$name joined", true)
                        }
                        EventType.StartTyping.ordinal -> {
                            val user = event.data as User
                            this.adapter?.addUserIsTyping(user)
                            scrollToBottomIfNearBottom()
                        }
                        EventType.StopTyping.ordinal -> {
                            val user = event.data as User
                            this.adapter?.removeUserIsTyping(user)
                        }
                    }
                }, {
                    it
                })
    }

    private fun setupTypingListener() {
        isTypingDisposable = mmInputFrag?.getEditText()?.textChanges()
                ?.compose(bindToLifecycle())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe({
                    if (it.isNotEmpty()) {
                        chatController?.userStartedTyping(true)
                    }
                }, {
                    it
                })
    }

    private fun setupRecyclerView() {
        // setup adapter
        if (adapter == null) {
            context?.let { adapter = ChatAdapter(it, messageList) }
        }
        chatRecyclerView.adapter = adapter
        chatRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = chatRecyclerView.layoutManager as? LinearLayoutManager

                if (layoutManager?.findLastCompletelyVisibleItemPosition() == ((adapter?.itemCount ?: 0) - 1)) {
                    // is scrolled to bot
                }
            }
        })
    }

    private fun setupMMLib() {
        mmInputFrag = MMInputFragment.getMMInputFieldInstance(childFragmentManager, R.id.mmInputField)
        mmInputFrag?.setup(this)
    }

    private fun shouldShowLoginPage() {
        Realm.getDefaultInstance().use { realm ->
            if (!SessionController.getInstance().isUserLoggedIn(realm)) {
                mainActivity?.navigateToFragment(LoginFragment(), shouldAddToContainer = true)
            }
        }
    }

    override fun sendMMData(mmData: MMData) {
        // customize mmdata
        mmData.sender_id = SessionController.getInstance().getUser()?.id
        mmData.sender_name = SessionController.getInstance().getUser()?.name
        mmData.date = System.currentTimeMillis()

        // send message to server
        sendMessageToServer(mmData)

        // update ui
        messageList?.add(mmData)
        adapter?.notifyDataSetChanged()
        scrollToBottomPost()
    }

    private fun fetchLatestMessages() {
        chatController?.getLatestMessages(this)
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe({ messagesResponse ->
                    this.messageList?.clear()
                    this.messageList?.addAll(messagesResponse)
                    adapter?.notifyDataSetChanged()
                    this.scrollToBottomPost()
                }, {})
    }

    private fun sendMessageToServer(mmData: MMData) {
        chatController?.sendMessageToServer(mmData)
                ?.compose(bindToLifecycle())
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe({
                    it
                }, {
                    // remove mmData from list since it failed to send
                    val didRemoveMMData = messageList?.remove(mmData)
                    if (didRemoveMMData == true) adapter?.notifyDataSetChanged()
                    MMToast.showToast(context, "Sending failed ${it.localizedMessage}", Toast.LENGTH_LONG)
                })
    }

    private fun scrollToBottomIfNearBottom(isNewElementAdded: Boolean = false) {
        val layoutManager = chatRecyclerView?.layoutManager as? LinearLayoutManager

        val itemCount = layoutManager?.itemCount ?: 0
        val lastVisiblePosition = layoutManager?.findLastVisibleItemPosition() ?: 0

        val offset = if (isNewElementAdded) 3 else 2
        if (lastVisiblePosition >= itemCount - offset) {
            scrollToBottomPost()
        }
    }

    private fun scrollToBottomPost() {
        chatRecyclerView.post {
            chatRecyclerView.scrollToPosition(adapter?.itemCount?.minus(1) ?: 0)
//            saveSharedListPositionValues()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        chatController?.stopServerConnection()
        if (this.newMessageDisposable?.isDisposed == false) {
            this.newMessageDisposable?.dispose()
        }
        if (this.isTypingDisposable?.isDisposed == false) {
            this.isTypingDisposable?.dispose()
        }
    }

    override fun onStart() {
        super.onStart()
        if (this.newMessageDisposable?.isDisposed == true) {
            setupNewEventListener()
        }
        if (this.isTypingDisposable?.isDisposed == true) {
            setupTypingListener()
        }
    }
}