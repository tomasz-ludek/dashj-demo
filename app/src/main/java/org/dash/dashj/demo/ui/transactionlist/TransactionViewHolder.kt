package org.dash.dashj.demo.ui.transactionlist

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.transaction_list_row.*
import org.bitcoinj.core.Address
import org.bitcoinj.core.Transaction
import org.bitcoinj.wallet.Wallet


class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), LayoutContainer {

    override val containerView: View?
        get() = itemView

    @SuppressLint("SetTextI18n")
    fun bind(transaction: Transaction, wallet: Wallet) {
        hashView.text = transaction.hashAsString
        val value = transaction.getValue(wallet)
        amountView.text = value.toFriendlyString()
        feeView.text = transaction.fee?.toFriendlyString()
        confidenceView.text = "${transaction.confidence.confidenceType.name}\n${transaction.confidence}"
        purposeView.text = transaction.purpose.name

        var destAddress: Address? = null
        val toAddresses = mutableListOf<Address>()

        for (transactionOutput in transaction.outputs) {
            val toAddress = Address.fromBase58(
                    wallet.networkParameters,
                    transactionOutput
                            .scriptPubKey
                            .getToAddress(wallet.networkParameters)
                            .toBase58()
            )
            if (!transactionOutput.isMine(wallet)) {
                destAddress = toAddress
            }
            if (toAddress !== getNullAddress(wallet.networkParameters)) {
                toAddresses.add(toAddress)
            }
        }
        if (destAddress != null) {
            addressView.text = destAddress.toString()
        } else {
            addressView.text = toAddresses.joinToString(separator = "\n")
        }
    }

    private fun getNullAddress(network: org.bitcoinj.core.NetworkParameters): Address {
        val numAddressBytes = 20
        val bytes = ByteArray(numAddressBytes)
        return Address(network, bytes)
    }
}
