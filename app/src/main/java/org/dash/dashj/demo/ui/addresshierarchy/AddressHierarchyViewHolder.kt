package org.dash.dashj.demo.ui.addresshierarchy

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.addresses_hierarchy_row.*
import org.bitcoinj.core.ECKey
import org.bitcoinj.crypto.DeterministicKey
import org.bitcoinj.wallet.Wallet

class AddressHierarchyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), LayoutContainer {

    override val containerView: View?
        get() = itemView

    fun bind(key: ECKey, wallet: Wallet) {
        if (key is DeterministicKey) {
            pathView.text = key.pathAsString
        } else {
            pathView.text = "N/A"
        }

        addressView.text = key.toAddress(wallet.networkParameters).toBase58()
        pubView.text = key.publicKeyAsHex
    }

}
