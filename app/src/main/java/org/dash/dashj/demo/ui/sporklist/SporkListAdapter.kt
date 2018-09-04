package org.dash.dashj.demo.ui.sporklist

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import org.bitcoinj.core.SporkManager
import org.bitcoinj.core.SporkMessage
import org.dash.dashj.demo.R

class SporkListAdapter(context: Context, sporkManager: SporkManager) : RecyclerView.Adapter<SporkViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private val sporks = mutableListOf<SporkMessage>()
    private var sporkManager: SporkManager

    init {
        setHasStableIds(true)
        this.sporkManager = sporkManager
    }

    fun clear() {
        sporks.clear()
        notifyDataSetChanged()
    }

    fun replace(peers: List<SporkMessage>?) {
        this.sporks.clear()
        peers?.let { this.sporks.addAll(it) }

        notifyDataSetChanged()
    }

    private fun getItem(position: Int): SporkMessage {
        return sporks[position]
    }

    override fun getItemCount(): Int {
        return sporks.size
    }

    override fun getItemId(position: Int): Long {
        return sporks[position].hash.toBigInteger().toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SporkViewHolder {
        val itemView = inflater.inflate(R.layout.spork_list_row, parent, false)
        return SporkViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SporkViewHolder, position: Int) {
        val spork = getItem(position)
        val isActive = sporkManager.isSporkActive(spork.sporkID)
        holder.bind(spork, isActive)
    }
}
