package com.buur.frederik.multimediechatexample.fragments.chatfragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.buur.frederik.multimediechat.models.MMData
import com.buur.frederik.multimediechat.views.inputfield.ISendMessage
import com.buur.frederik.multimediechatexample.R
import com.buur.frederik.multimediechatexample.dummybackend.SampleData
import com.buur.frederik.multimediechatexample.fragments.MMFragment
import kotlinx.android.synthetic.main.fragment_chat.*
import android.app.Activity
import com.buur.frederik.multimediechat.enums.MMDataType
import com.buur.frederik.multimediechat.views.inputfield.MMInputFieldView


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
        this.mainActivity?.let { mmInputField.setup(it, mmView, this, this) }
    }

    override fun sendMMData(mmData: MMData) {

        sendToDummyBackend(mmData)

        // not necessary when using dummy backend
        //messageList?.add(mmData)

        adapter?.notifyDataSetChanged()
        scrollToBottomPost()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == MMInputFieldView.GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            mmInputField.convertToMMData(data, MMDataType.Image)
            // do your logic here...
        }
        super.onActivityResult(requestCode, resultCode, data)
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