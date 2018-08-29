package org.dash.dashj.demo.ui.masternodelist

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.masternode_list_row.*
import org.bitcoinj.core.Masternode
import java.util.*

class MasternodeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), LayoutContainer {

    override val containerView: View?
        get() = itemView

    @SuppressLint("SetTextI18n")
    fun bind(masternode: Masternode) {

        val masternodeInfo = masternode.info

        itemNoView.text = "#" + adapterPosition.toString()
        addressView.text = masternodeInfo.address.toString()
        activeStateView.text = masternodeInfo.activeState.toString()
        lastDsqView.text = masternodeInfo.nLastDsq.toString()
        lastCheckedView.text = Date(masternodeInfo.nTimeLastChecked).toString()
        lastPaidView.text = Date(masternodeInfo.nTimeLastChecked).toString()
        lastPingView.text = Date(masternodeInfo.nTimeLastPing * 1000).toString()
    }
}
