package org.dash.dashj.demo.ui.adapter.holder

import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.peer_list_row.*
import org.bitcoinj.core.Peer
import org.dash.dashj.demo.R

class PeerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), LayoutContainer {

    override val containerView: View?
        get() = itemView

    fun bind(peer: Peer) {
        val versionMessage = peer.peerVersionMessage
        val isDownloading = peer.isDownloadData

        val address = peer.address.addr
        ipView.text = address.hostAddress

        val bestHeight = peer.bestHeight
        heightView.text = if (bestHeight > 0) getString(R.string.peer_list_row_blocks, bestHeight) else null
        heightView.typeface = if (isDownloading) Typeface.DEFAULT_BOLD else Typeface.DEFAULT

        versionView.text = versionMessage.subVer
        versionView.typeface = if (isDownloading) Typeface.DEFAULT_BOLD else Typeface.DEFAULT

        protocolView.text = getString(R.string.peer_list_row_protocol, versionMessage.clientVersion)
        protocolView.typeface = if (isDownloading) Typeface.DEFAULT_BOLD else Typeface.DEFAULT

        val pingTime = peer.pingTime
        pingView.text = if (pingTime < java.lang.Long.MAX_VALUE) getString(R.string.peer_list_row_ping_time, pingTime) else null
        pingView.typeface = if (isDownloading) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
    }

    private fun getString(resId: Int, vararg formatArgs: Any): String {
        return itemView.context.getString(resId, *formatArgs)
    }
}
