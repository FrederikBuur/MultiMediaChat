package com.buur.frederik.multimediechatexample.fragments.chatfragment

import android.os.Bundle
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
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_chat.*

class ChatFragment : MMFragment(), ISendMessage {

    private var adapter: ChatAdapter? = null
    private var messageList: ArrayList<MMData>? = null

    private var chatController: ChatController? = null
    private var newMessageDisposable: Disposable? = null

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
        chatController?.establishServerConnection(context)

        setupRecyclerView()
        setupMMLib()

        scrollToBottomPost()
        setupNewMessageListener()

    }

    private fun setupNewMessageListener() {
        newMessageDisposable = chatController?.newMessagesPublisher()
                ?.compose(bindToLifecycle())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe({ mmData ->
                    messageList?.add(mmData)
                    adapter?.notifyDataSetChanged()
                    scrollToBottomPost()
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

    }

    private fun setupMMLib() {

        MMInputFragment.getMMInputFieldInstance(childFragmentManager, R.id.mmInputField)?.setup(this)

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

    private fun scrollToBottomPost() {
        chatRecyclerView.post {
            chatRecyclerView.scrollToPosition(messageList?.size?.minus(1) ?: 0)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelableArrayList(MESSAGE_LIST_KEY, messageList)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (this.newMessageDisposable?.isDisposed == false) {
            this.newMessageDisposable?.dispose()
        }
    }

    override fun onStart() {
        super.onStart()
        if (this.newMessageDisposable?.isDisposed == true) {
            setupNewMessageListener()
        }

    }

    companion object {
        private const val MESSAGE_LIST_KEY = "message_list"
    }

}