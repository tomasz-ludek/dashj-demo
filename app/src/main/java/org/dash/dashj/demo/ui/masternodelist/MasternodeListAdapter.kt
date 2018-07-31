package org.dash.dashj.demo.ui.masternodelist

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import org.bitcoinj.core.Masternode
import org.bitcoinj.core.MasternodeManager
import org.dash.dashj.demo.R

class MasternodeListAdapter(context: Context, masternodeManager: MasternodeManager)
    : RecyclerView.Adapter<MasternodeViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private val masternodes = mutableListOf<Masternode>()
    private var masternodeManager: MasternodeManager

    init {
        setHasStableIds(true)
        this.masternodeManager = masternodeManager
    }

    fun clear() {
        masternodes.clear()
        notifyDataSetChanged()
    }

    fun replace(masternodes: List<Masternode>?) {
        this.masternodes.clear()
        masternodes?.let { this.masternodes.addAll(it) }

        notifyDataSetChanged()
    }

    private fun getItem(position: Int): Masternode {
        return masternodes[position]
    }

    override fun getItemCount(): Int {
        return masternodes.size
    }

    override fun getItemId(position: Int): Long {
        return masternodes[position].hash.toBigInteger().toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MasternodeViewHolder {
        val itemView = inflater.inflate(R.layout.spork_list_row, parent, false)
        return MasternodeViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MasternodeViewHolder, position: Int) {
        val masternode = getItem(position)
        holder.bind(masternode)
    }
}
