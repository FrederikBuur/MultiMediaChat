package com.buur.frederik.multimediechatexample.fragments.chatfragment

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.buur.frederik.multimediechat.enums.MMDataType
import com.buur.frederik.multimediechat.models.MMData
import com.buur.frederik.multimediechat.views.*

class ChatAdapter(var context: Context, var list: ArrayList<MMData>?) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {

        return ChatViewHolder( when(viewType) {

            MMDataType.Audio.ordinal -> AudioView(context)
            MMDataType.Video.ordinal -> VideoView(context)
            MMDataType.Image.ordinal -> ImgView(context)
            MMDataType.Gif.ordinal -> GifView(context)
            MMDataType.Text.ordinal -> TextMessageView(context)
            else -> TextMessageView(context)

        })

    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {

        val itemView = holder.itemView
        val mmData = getMMData(position)

        when(itemView) {
            is SuperView -> itemView.setup(true, mmData.source)
            else -> {}
        }

    }

    override fun getItemCount(): Int {
        return list?.count() ?: 0
    }

    private fun getMMData(position: Int): MMData {
        return list?.let {
            it[position]
        } ?: MMData("No Data", MMDataType.Text.ordinal)
    }

    inner class ChatViewHolder(container: SuperView) : RecyclerView.ViewHolder(container) {
        init {
        }
    }

}