package org.dash.dashj.demo.ui.masternodelist

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import org.bitcoinj.core.Masternode
import org.dash.dashj.demo.R

class MasternodeListAdapter(context: Context)
    : RecyclerView.Adapter<MasternodeViewHolder>(), Filterable {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private val masternodes = mutableListOf<Masternode>()
    private val refMasternodes = mutableListOf<Masternode>()

    init {
        setHasStableIds(true)
    }

    fun clear() {
        masternodes.clear()
        this.refMasternodes.clear()
    }

    fun replace(masternodes: List<Masternode>?) {
        clear()
        masternodes?.also {
            this.masternodes.addAll(it)
            this.refMasternodes.addAll(it)
        }

        notifyDataSetChanged()
    }

    private fun getItem(position: Int): Masternode {
        return masternodes[position]
    }

    override fun getItemCount(): Int {
        return masternodes.size
    }

    override fun getItemId(position: Int): Long {
        val address = masternodes[position].info.address.toString()
        return address.replace(".", "").replace(":", "").toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MasternodeViewHolder {
        val itemView = inflater.inflate(R.layout.masternode_list_row, parent, false)
        return MasternodeViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MasternodeViewHolder, position: Int) {
        val masternode = getItem(position)
        holder.bind(masternode)
    }

    override fun getFilter(): Filter {
        return object : MasternodeListFilter(refMasternodes) {
            override fun publishResults(masternodeFilteredList: List<Masternode>) {
                masternodes.clear()
                masternodes.addAll(masternodeFilteredList)
                notifyDataSetChanged()
            }
        }
    }
}
