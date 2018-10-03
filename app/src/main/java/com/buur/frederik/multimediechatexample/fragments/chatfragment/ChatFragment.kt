package com.buur.frederik.multimediechatexample.fragments.chatfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.buur.frederik.multimediechat.models.MMData
import com.buur.frederik.multimediechat.views.inputfield.ISendMessage
import com.buur.frederik.multimediechat.views.inputfield.MMInputFieldView
import com.buur.frederik.multimediechatexample.R
import com.buur.frederik.multimediechatexample.dummybackend.SampleData
import com.buur.frederik.multimediechatexample.fragments.MMFragment
import kotlinx.android.synthetic.main.fragment_chat.*

class ChatFragment: MMFragment(), ISendMessage {

    private var adapter: ChatAdapter? = null
    private var messageList: ArrayList<MMData>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setup()

    }

    private fun setup() {

        // dummy data
        messageList = SampleData.populateDummyData()

        setupRecyclerView()
        setupMMLib()


        scrollToBottomPost()

    }

    private fun setupRecyclerView() {
        // setup adapter
        if (adapter == null) {
            context?.let {adapter = ChatAdapter(it, messageList)}
        }
        chatRecyclerView.adapter = adapter

//        chatRecyclerView?.layoutChangeEvents()
//                ?.compose(bindToLifecycle())
//                ?.doOnNext {
//                    //scroll to bottom when keyboard opens if bottom element is showing
//                }
//                ?.subscribe({}, {})

    }

    private fun setupMMLib() {

        mainActivity?. let {
            MMInputFieldView.getMMInputFieldInstance(childFragmentManager, R.id.mmInputField)?.setup(it, mmView, this)
        }

    }

    override fun sendMMData(mmData: MMData) {

        sendToDummyBackend(mmData)

        // not necessary when using dummy backend
        //messageList?.add(mmData)

        adapter?.notifyDataSetChanged()
        scrollToBottomPost()

    }

    private fun sendToDummyBackend(mmData: MMData) {
        SampleData.dummyData?.add(mmData)
    }

    private fun scrollToBottomPost() {
        chatRecyclerView.post {
            chatRecyclerView.scrollToPosition(messageList?.size?.minus(1) ?: 0)
        }
    }

}