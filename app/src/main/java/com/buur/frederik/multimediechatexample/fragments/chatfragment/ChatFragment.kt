package com.buur.frederik.multimediechatexample.fragments.chatfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.buur.frederik.multimediechat.models.MMData
import com.buur.frederik.multimediechatexample.R
import com.buur.frederik.multimediechatexample.dummybackend.SampleData
import com.buur.frederik.multimediechatexample.fragments.MMFragment
import kotlinx.android.synthetic.main.fragment_chat.*

class ChatFragment: MMFragment() {

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
        messageList = SampleData.dummyData

        if (adapter == null) {
            context?.let {
                adapter = ChatAdapter(it, messageList)
            }
        }

        chatRecyclerView.adapter = adapter

    }

}