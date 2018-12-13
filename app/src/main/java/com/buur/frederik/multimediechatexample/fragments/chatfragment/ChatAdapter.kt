package com.buur.frederik.multimediechatexample.fragments.chatfragment

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.widget.FrameLayout
import com.buur.frederik.multimediechat.enums.MMDataType
import com.buur.frederik.multimediechat.models.MMData
import com.buur.frederik.multimediechat.messageviews.*
import com.buur.frederik.multimediechatexample.controllers.SessionController
import com.buur.frederik.multimediechatexample.models.User

class ChatAdapter(var context: Context, var list: ArrayList<MMData>?) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    var usersTyping = ArrayList<User>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {

        return ChatViewHolder(when (viewType) {

            MMDataType.Audio.ordinal -> AudioView(context)
            MMDataType.Video.ordinal -> VideoView(context)
            MMDataType.Image.ordinal, MMDataType.Gif.ordinal -> ImgView(context)
            MMDataType.Text.ordinal -> TextMessageView(context)
            MMDataType.Document.ordinal -> DocumentView(context)
            else -> {
                UserIsTypingView(context)
            }

        })

    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {

        val itemView = holder.itemView
        val mmData = getMMData(position)
        val previousMMData = getMMData(position - 1)
        val isSender = isMMDataMsgSender(mmData)

        when (itemView) {
            is SuperView -> {
                itemView.setup(isSender, mmData, previousMMData)
            }
            is UserIsTypingView -> {
                itemView.setup(this.usersTyping)
            }
            else -> {
            }
        }

    }

    fun addUserIsTyping(user: User) {
        usersTyping.forEach {
            if (user.id == it.id) {
                return
            }
        }
        usersTyping.add(user)
        notifyDataSetChanged()
    }

    fun removeUserIsTyping(user: User) {
        var index: Int? = null
        usersTyping.forEachIndexed { i, u ->
            if (u.id == user.id) {
                index = i
                return@forEachIndexed
            }
        }
        index?.let {
            usersTyping.removeAt(it)
            notifyDataSetChanged()
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = getMMData(position)
        return item?.type ?: -1
    }

    override fun getItemCount(): Int {
        return list?.count()?.plus(1) ?: 0 // plus one for user typing view
    }

    private fun isMMDataMsgSender(mmData: MMData?): Boolean {
        val id = SessionController.getInstance().getUser()?.id ?: -1
        return mmData?.sender_id?.equals(id) == true
    }

    private fun getMMData(position: Int): MMData? {
        return list?.getOrNull(position)
    }

    inner class ChatViewHolder(container: FrameLayout) : RecyclerView.ViewHolder(container) {
        init {
        }
    }

}