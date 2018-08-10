package org.dash.dashj.demo.ui.governancelist

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.governance_list_row.*
import org.bitcoinj.governance.GovernanceObject
import org.dash.dashj.demo.Utils
import org.dash.dashj.demo.util.GovernanceHelper


class GovernanceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), LayoutContainer {

    override val containerView: View?
        get() = itemView

    @SuppressLint("SetTextI18n")
    fun bind(governanceObject: GovernanceObject) {
        itemNoView.text = "#" + adapterPosition.toString()

        val typeAndData = GovernanceHelper.extractTypeAndData(governanceObject.dataAsPlainString)
        val type = typeAndData.first

        val isProposal = type == "proposal"

        nameRowView.visibility = if (isProposal) View.VISIBLE else View.GONE
        startDateRowView.visibility = if (isProposal) View.VISIBLE else View.GONE
        endDateRowView.visibility = if (isProposal) View.VISIBLE else View.GONE

        if (isProposal) {
            val proposal = GovernanceHelper.parseProposal(typeAndData.second)
            nameView.text = proposal.name
            startDateView.text = Utils.format(proposal.startDate)
            endDateView.text = Utils.format(proposal.endDate)
        }

        typeView.text = type
        hashView.text = governanceObject.hash.toString()
        dataView.text = governanceObject.dataAsPlainString
    }
}
