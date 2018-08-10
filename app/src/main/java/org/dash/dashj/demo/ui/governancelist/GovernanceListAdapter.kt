package org.dash.dashj.demo.ui.governancelist

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import org.bitcoinj.governance.GovernanceManager
import org.bitcoinj.governance.GovernanceObject
import org.dash.dashj.demo.R

class GovernanceListAdapter(context: Context, governanceManager: GovernanceManager)
    : RecyclerView.Adapter<GovernanceViewHolder>(), Filterable {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private val governanceObjects = mutableListOf<GovernanceObject>()
    private val refGovernanceObjects = mutableListOf<GovernanceObject>()
    private var governanceManager: GovernanceManager

    init {
        setHasStableIds(true)
        this.governanceManager = governanceManager
    }

    fun clear() {
        governanceObjects.clear()
        this.refGovernanceObjects.clear()
    }

    fun replace(masternodes: List<GovernanceObject>?) {
        clear()
        masternodes?.also {
            this.governanceObjects.addAll(it)
            this.refGovernanceObjects.addAll(it)
        }

        notifyDataSetChanged()
    }

    private fun getItem(position: Int): GovernanceObject {
        return governanceObjects[position]
    }

    override fun getItemCount(): Int {
        return governanceObjects.size
    }

    override fun getItemId(position: Int): Long {
        return governanceObjects[position].hash.toBigInteger().toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GovernanceViewHolder {
        val itemView = inflater.inflate(R.layout.governance_list_row, parent, false)
        return GovernanceViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: GovernanceViewHolder, position: Int) {
        val governanceObject = getItem(position)
        holder.bind(governanceObject)
    }

    override fun getFilter(): Filter {
        return object : GovernanceListFilter(refGovernanceObjects) {
            override fun publishResults(masternodeFilteredList: List<GovernanceObject>) {
                governanceObjects.clear()
                governanceObjects.addAll(masternodeFilteredList)
                notifyDataSetChanged()
            }
        }
    }
}
