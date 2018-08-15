package org.dash.dashj.demo.ui.governancelist

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.governance_list_row.*
import org.bitcoinj.governance.GovernanceObject
import org.dash.dashj.demo.R
import org.dash.dashj.demo.Utils
import org.dash.dashj.demo.VoteActivity
import org.dash.dashj.demo.util.GovernanceHelper


class GovernanceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), LayoutContainer {

    override val containerView: View?
        get() = itemView

    @SuppressLint("SetTextI18n")
    fun bind(governanceObject: GovernanceObject) {
        itemNoView.text = "#" + adapterPosition.toString()

        val isProposal = (governanceObject.objectType == GovernanceObject.GOVERNANCE_OBJECT_PROPOSAL)

        nameRowView.visibility = if (isProposal) View.VISIBLE else View.GONE
        startDateRowView.visibility = if (isProposal) View.VISIBLE else View.GONE
        endDateRowView.visibility = if (isProposal) View.VISIBLE else View.GONE

        if (isProposal) {
            val proposal = GovernanceHelper.parseProposal(governanceObject)
            nameView.text = proposal.name
            startDateView.text = Utils.format(proposal.startDate)
            endDateView.text = Utils.format(proposal.endDate)

            itemView.setOnClickListener {
                showOptionsDialog(governanceObject, proposal)
            }
        } else {
            itemView.setOnClickListener(null)
        }

        typeView.text = typeLabel(governanceObject.objectType)
        hashView.text = governanceObject.hash.toString()
        dataView.text = governanceObject.dataAsPlainString
    }

    private fun typeLabel(type: Int): String {
        return when (type) {
            GovernanceObject.GOVERNANCE_OBJECT_UNKNOWN -> "unknown"
            GovernanceObject.GOVERNANCE_OBJECT_PROPOSAL -> "proposal"
            GovernanceObject.GOVERNANCE_OBJECT_TRIGGER -> "trigger"
            GovernanceObject.GOVERNANCE_OBJECT_WATCHDOG -> "watchdog"
            else -> "unsupported"
        }
    }

    private fun showOptionsDialog(governanceObject: GovernanceObject, proposalData: GovernanceProposalData) {
        val builderSingle = AlertDialog.Builder(itemView.context)
        builderSingle.setTitle(proposalData.name)

        val arrayAdapter = ArrayAdapter<String>(itemView.context, R.layout.select_dialog_item)
        arrayAdapter.add("Show details in browser")
        arrayAdapter.add("Vote on proposal")

        builderSingle.setNegativeButton("cancel") { dialog, _ -> dialog.dismiss() }
        builderSingle.setAdapter(arrayAdapter) { _, which ->
            when (which) {
                0 -> openProposalUrl(proposalData.url)
                1 -> voteOnProposal(governanceObject)
            }
        }
        builderSingle.show()
    }

    private fun voteOnProposal(governanceObject: GovernanceObject) {
        val proposalDate = GovernanceHelper.extractData(governanceObject.dataAsPlainString)
        val proposalHash = governanceObject.hash.toString()
        itemView.context.startActivity(VoteActivity.createIntent(itemView.context, proposalHash, proposalDate))
    }

    private fun openProposalUrl(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        itemView.context.startActivity(browserIntent)
        if (browserIntent.resolveActivity(itemView.context.packageManager) != null) {
            itemView.context.startActivity(browserIntent)
        } else {
            Toast.makeText(itemView.context, "No application can handle this request.", Toast.LENGTH_LONG).show();
        }
    }
}
