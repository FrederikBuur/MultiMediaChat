package com.buur.frederik.multimediechat.views.messagerecyclerview

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import com.buur.frederik.multimediechat.models.MMData

class MMAdapter(var context: Context, var list: ArrayList<MMData>) : RecyclerView.Adapter<MMAdapter.MMViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewTpye: Int): MMViewHolder {
        return MMViewHolder(FrameLayout(context))
    }

    override fun onBindViewHolder(holder: MMViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return list.count()
    }

    inner class MMViewHolder(container: FrameLayout) : RecyclerView.ViewHolder(container) {
        init {
        }
    }

}