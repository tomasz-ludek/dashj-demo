package org.dash.dashj.demo.ui.evouserlist

import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ArrayAdapter
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.evo_user_list_row.*
import org.bitcoinj.evolution.EvolutionUser
import org.dash.dashj.demo.R

class EvoUserListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), LayoutContainer {

    override val containerView: View?
        get() = itemView

    fun bind(evoUser: EvolutionUser, listener: EvoUserListAdapter.OnOptionClickListener) {
        userNameView.text = evoUser.userName
        topUpCreditsView.text = evoUser.topUpCredits.toFriendlyString()
        spentCreditsView.text = evoUser.spentCredits.toFriendlyString()
        creditBalanceView.text = evoUser.creditBalance.toFriendlyString()
        itemView.setOnClickListener {
            showOptionsDialog(evoUser, listener)
        }
    }

    private fun showOptionsDialog(evoUser: EvolutionUser, listener: EvoUserListAdapter.OnOptionClickListener) {
        val builderSingle = AlertDialog.Builder(itemView.context)
        builderSingle.setTitle(evoUser.userName)

        val arrayAdapter = ArrayAdapter<String>(itemView.context, R.layout.select_dialog_item)
        arrayAdapter.add("Top-up")
        arrayAdapter.add("Reset")

        builderSingle.setNegativeButton("cancel") { dialog, _ -> dialog.dismiss() }
        builderSingle.setAdapter(arrayAdapter) { _, which ->
            when (which) {
                0 -> listener.onTopUp(evoUser)
                1 -> listener.onReset(evoUser)
            }
        }
        builderSingle.show()
    }
}
