package com.buur.frederik.multimediechat.gifpicker

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.buur.frederik.multimediechat.models.gif.GifData

class GifAdapter(var context: Context, var gifList: ArrayList<GifData>?, var gifDelegate: IGifOnClick) : RecyclerView.Adapter<GifAdapter.GifViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GifViewHolder {

        return GifViewHolder(GifView(context))

    }

    override fun onBindViewHolder(holder: GifViewHolder, position: Int) {

        val itemView = holder.itemView
        val gifData = gifList?.get(position)

        (itemView as? GifView)?.setup(gifData, gifDelegate)

    }

    override fun getItemCount(): Int {
        return gifList?.count() ?: 0
    }

    inner class GifViewHolder(container: GifView) : RecyclerView.ViewHolder(container) {
        init {
        }
    }
}