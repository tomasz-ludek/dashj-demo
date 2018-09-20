package org.dash.dashj.demo.ui.addresshierarchy

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.addresses_hierarchy_row.*
import org.bitcoinj.core.ECKey
import org.bitcoinj.core.NetworkParameters
import org.bitcoinj.crypto.DeterministicKey

class AddressHierarchyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), LayoutContainer {

    override val containerView: View?
        get() = itemView

    fun bind(key: ECKey, networkParameters: NetworkParameters) {
        if (key is DeterministicKey) {
            pathView.text = key.pathAsString
        } else {
            pathView.text = "N/A"
        }

        addressView.text = key.toAddress(networkParameters).toBase58()
        pubView.text = key.publicKeyAsHex
    }

}
