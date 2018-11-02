package com.buur.frederik.multimediechatexample.fragments.chatfragment

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.buur.frederik.multimediechat.enums.MMDataType
import com.buur.frederik.multimediechat.models.MMData
import com.buur.frederik.multimediechat.messageviews.*
import com.buur.frederik.multimediechatexample.controllers.SessionController

class ChatAdapter(var context: Context, var list: ArrayList<MMData>?) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {

        return ChatViewHolder( when(viewType) {

            MMDataType.Audio.ordinal -> AudioView(context)
            MMDataType.Video.ordinal -> VideoView(context)
            MMDataType.Image.ordinal, MMDataType.Gif.ordinal -> ImgView(context)
            MMDataType.Text.ordinal -> TextMessageView(context)
            MMDataType.Document.ordinal -> DocumentView(context)
            else -> {
                TextMessageView(context)
            }

        })

    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {

        val itemView = holder.itemView
        val mmData = getMMData(position)
        val isSender = isMMDataMsgSender(mmData)

        when(itemView) {
            is ImgView -> {
                itemView.setup(isSender, mmData)
            }
            is VideoView -> {
                itemView.setup(isSender, mmData)
            }
            is TextMessageView -> {
                itemView.setup(isSender, mmData)
            }
            is AudioView -> {
                itemView.setup(isSender, mmData)
            }
            is DocumentView -> {
                itemView.setup(isSender, mmData)
            }
            else -> {
            }
        }

    }

    override fun getItemViewType(position: Int): Int {

        val item = getMMData(position)

        return item.type
    }

    override fun getItemCount(): Int {
        return list?.count() ?: 0
    }

    private fun isMMDataMsgSender(mmData: MMData): Boolean {
        val id = SessionController.getInstance().getUser()?.id ?: -1
        return mmData.sender_id?.equals(id) == true
    }

    private fun getMMData(position: Int): MMData {
        return list?.let {
            it[position]
        } ?: MMData(-1, "No Data", MMDataType.Text.ordinal)
    }

    inner class ChatViewHolder(container: SuperView) : RecyclerView.ViewHolder(container) {
        init {
        }
    }

}