package com.buur.frederik.multimediechatexample.fragments.chatfragment

import android.content.res.Configuration
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.buur.frederik.multimediechat.models.MMData
import com.buur.frederik.multimediechat.inputfield.ISendMessage
import com.buur.frederik.multimediechat.inputfield.MMInputFragment
import com.buur.frederik.multimediechatexample.R
import com.buur.frederik.multimediechatexample.controllers.SessionController
import com.buur.frederik.multimediechatexample.dummybackend.SampleData
import com.buur.frederik.multimediechatexample.fragments.MMFragment
import com.buur.frederik.multimediechatexample.fragments.loginfragment.LoginFragment
import com.buur.frederik.multimediechatexample.models.User
import com.jakewharton.rxbinding2.view.layoutChangeEvents
import com.jakewharton.rxbinding2.view.layoutChanges
import com.jakewharton.rxbinding2.widget.textChanges
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_chat.*

class ChatFragment : MMFragment(), ISendMessage {

    private var adapter: ChatAdapter? = null
    private var messageList: ArrayList<MMData>? = null

    private var mmInputFrag: MMInputFragment? = null

    private var chatController: ChatController? = null
    private var newMessageDisposable: Disposable? = null
    private var isTypingDisposable: Disposable? = null
    private var resizeContainerDisposable: Disposable? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        savedInstanceState?.let { state ->
            val restoredList = state.getParcelableArrayList<MMData>(MESSAGE_LIST_KEY)
            messageList = restoredList
        } ?: kotlin.run {
            messageList = SampleData.populateDummyData()
            shouldShowLoginPage()
        }

        setupViews()
    }

    private fun setupViews() {

        if (chatController == null) {
            chatController = ChatController()
        }
        chatController?.startServerConnection(context)

        setupRecyclerView()
        setupMMLib()

        scrollToBottomPost()
        setupNewEventListener()
        setupTypingListener()
        setupResizeListener()
    }

    private fun setupResizeListener() {
        resizeContainerDisposable = chatFragmentContainer?.layoutChanges()
                ?.compose(bindToLifecycle())
                ?.doOnNext {
                    //scrollToBottomIfNeeded()
                }
                ?.subscribe({}, {})

        val dips = chatRecyclerView.layoutChangeEvents()
                .compose(bindToLifecycle())
                .doOnNext {
                    if (it.bottom() < it.oldBottom())
                    scrollToBottomIfNeeded()
                }
                .subscribe({}, {})
    }

    private fun setupNewEventListener() {
        newMessageDisposable = chatController?.newMessagesPublisher()
                ?.compose(bindToLifecycle())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe({ event ->
                    val type = event.type
                    when (type) {
                        EventType.Message.ordinal -> {
                            val mmData = event.data as MMData
                            messageList?.add(mmData)
                            adapter?.notifyDataSetChanged()
                            scrollToBottomIfNeeded()
                        }
                        EventType.UserConnected.ordinal -> {
                            val name = event.data as String
                            this.mainActivity?.showTopNotification("$name joined", true)
                        }
                        EventType.StartTyping.ordinal -> {
                            val user = event.data as User
                            this.adapter?.addUserIsTyping(user)
                            scrollToBottomIfNeeded()
//                            Toast.makeText(context, "${user.name} typing", Toast.LENGTH_SHORT).show()
                        }
                        EventType.StopTyping.ordinal -> {
                            val user = event.data as User
                            this.adapter?.removeUserIsTyping(user)
//                            Toast.makeText(context, "${user.name} stop typing", Toast.LENGTH_SHORT).show()
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
        //val lm = LinearLayoutManager(context)
        //lm.initialPrefetchItemCount = 5
        //chatRecyclerView.layoutManager = lm
        chatRecyclerView.adapter = adapter

    }

    private fun setupMMLib() {
        mmInputFrag = MMInputFragment.getMMInputFieldInstance(childFragmentManager, R.id.mmInputField)
        mmInputFrag?.setup(this)
    }

    private fun shouldShowLoginPage() {
        Realm.getDefaultInstance().use { realm ->
            if (!User.isLoggedIn(realm)) {
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

    private fun sendMessageToServer(mmData: MMData) {
        val disp = chatController?.sendMessageToServer(mmData)
                ?.compose(bindToLifecycle())
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe({
                    it
                }, {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                })
    }

    private fun scrollToBottomIfNeeded() {
        scrollToBottomPost()
    }

    private fun scrollToBottomPost() {
        chatRecyclerView.post {
            chatRecyclerView.scrollToPosition(adapter?.itemCount?.minus(1) ?: 0)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelableArrayList(MESSAGE_LIST_KEY, messageList)
        super.onSaveInstanceState(outState)
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

    companion object {
        private const val MESSAGE_LIST_KEY = "message_list"
    }

}