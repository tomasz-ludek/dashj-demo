package org.dash.dashj.demo.ui.peerlist

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import org.bitcoinj.core.Peer
import org.dash.dashj.demo.R
import java.net.InetAddress
import java.util.*

class PeerListAdapter(context: Context) : RecyclerView.Adapter<PeerViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private val peers = LinkedList<Peer>()
    private val hostNameCache = HashMap<InetAddress, String>()

    init {
        setHasStableIds(true)
    }

    fun clear() {
        peers.clear()
        notifyDataSetChanged()
    }

    fun updateHostNameCache(hostNameMap: Map<InetAddress, String>) {
        hostNameCache.clear()
        hostNameCache.putAll(hostNameMap)

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
        val hostName = hostNameCache[peer.address.addr]
        holder.bind(peer, hostName)
    }
}