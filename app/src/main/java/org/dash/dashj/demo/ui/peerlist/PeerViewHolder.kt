package org.dash.dashj.demo.ui.peerlist

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.peer_list_row.*
import org.bitcoinj.core.Peer
import org.dash.dashj.demo.R

class PeerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), LayoutContainer {

    override val containerView: View?
        get() = itemView

    fun bind(peer: Peer, hostName: String?) {
        val versionMessage = peer.peerVersionMessage
        itemView.setBackgroundResource(if (peer.isDownloadData) R.color.highlightBackground else R.color.transparent)

        val address = peer.address.addr
        ipView.text = address.hostAddress
        hostNameView.text = if (address.hostAddress != hostName) hostName else getString(R.string.peer_list_row_host_name_unknown)

        val bestHeight = peer.bestHeight
        heightView.text = if (bestHeight > 0) getString(R.string.peer_list_row_blocks, bestHeight) else null
        versionView.text = versionMessage.subVer
        protocolView.text = getString(R.string.peer_list_row_protocol, versionMessage.clientVersion)
        val pingTime = peer.pingTime
        pingView.text = if (pingTime < java.lang.Long.MAX_VALUE) getString(R.string.peer_list_row_ping_time, pingTime) else null
    }

    private fun getString(resId: Int, vararg formatArgs: Any): String {
        return itemView.context.getString(resId, *formatArgs)
    }

    private fun getString(resId: Int): String {
        return itemView.context.getString(resId)
    }
}
