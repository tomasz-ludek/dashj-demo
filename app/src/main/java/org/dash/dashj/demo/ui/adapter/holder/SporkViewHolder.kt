package org.dash.dashj.demo.ui.adapter.holder

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.spork_list_row.*
import org.bitcoinj.core.SporkMessage
import org.dash.dashj.demo.R
import java.util.*

class SporkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), LayoutContainer {

    override val containerView: View?
        get() = itemView

    fun bind(spork: SporkMessage, isActive: Boolean) {
        itemView.setBackgroundResource(if (isActive) R.color.highlightBackground else R.color.transparent)

        sporkIdView.text = spork.sporkID.toString()
        hashView.text = spork.hash.toString();
        timeView.text = Date(spork.timeSigned * 1000).toString()
        activeView.text = isActive.toString()
    }
}
