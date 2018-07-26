package org.dash.dashj.demo.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import org.bitcoinj.core.Peer
import org.dash.dashj.demo.R
import org.dash.dashj.demo.ui.adapter.holder.PeerViewHolder
import java.util.*

class PeerViewAdapter(context: Context) : RecyclerView.Adapter<PeerViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private val peers = LinkedList<Peer>()

    init {
        setHasStableIds(true)
    }

    fun clear() {
        peers.clear()
        notifyDataSetChanged()
    }

    fun replace(peers: List<Peer>?) {
        this.peers.clear()
        peers?.let { this.peers.addAll(it) }

        notifyDataSetChanged()
    }

    private fun getItem(position: Int): Peer {
        return peers[position]
    }

    override fun getItemCount(): Int {
        return peers.size
    }

    override fun getItemId(position: Int): Long {
        return peers[position].address.hashCode().toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeerViewHolder {
        val itemView = inflater.inflate(R.layout.peer_list_row, parent, false)
        return PeerViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PeerViewHolder, position: Int) {
        val peer = getItem(position)
        holder.bind(peer)
    }
}