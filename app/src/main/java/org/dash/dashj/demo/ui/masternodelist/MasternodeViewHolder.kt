package org.dash.dashj.demo.ui.masternodelist

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.masternode_list_row.*
import org.bitcoinj.core.Masternode

class MasternodeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), LayoutContainer {

    override val containerView: View?
        get() = itemView

    @SuppressLint("SetTextI18n")
    fun bind(masternode: Masternode) {

        itemNoView.text = "#" + adapterPosition.toString()
        hashView.text = masternode.hash.toString()
    }
}
