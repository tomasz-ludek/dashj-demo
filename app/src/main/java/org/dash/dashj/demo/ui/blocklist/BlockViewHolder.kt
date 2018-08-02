package org.dash.dashj.demo.ui.blocklist

import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.format.DateUtils
import android.text.style.TypefaceSpan
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.block_list_row.*
import org.bitcoinj.core.Address
import org.bitcoinj.core.ScriptException
import org.bitcoinj.core.StoredBlock
import org.bitcoinj.core.Transaction
import org.bitcoinj.wallet.Wallet
import org.dash.dashj.demo.R

class BlockViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), LayoutContainer {

    companion object {
        private const val ROW_BASE_CHILD_COUNT = 2
        private const val ROW_INSERT_INDEX = 1
    }

    private val textCoinBase: String
    private val textInternal: String

    init {
        textCoinBase = getString(R.string.block_list_coinbase)
        textInternal = getString(R.string.block_list_internal)
    }

    override val containerView: View?
        get() = itemView

    fun bind(storedBlock: StoredBlock, transactions: Set<Transaction>, wallet: Wallet) {
        val header = storedBlock.header
        miningRewardAdjustmentView.visibility = if (isMiningRewardHalvingPoint(storedBlock)) View.VISIBLE else View.GONE
        //        holder.miningDifficultyAdjustmentView
//                .setVisibility(isDifficultyTransitionPoint(storedBlock) ? View.VISIBLE : View.GONE);

        val height = storedBlock.height
        heightView.text = height.toString()

        val timeMs = header.timeSeconds * DateUtils.SECOND_IN_MILLIS
        if (timeMs < System.currentTimeMillis() - DateUtils.MINUTE_IN_MILLIS) {
            timeView.text = DateUtils.getRelativeDateTimeString(itemView.context, timeMs, DateUtils.MINUTE_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, 0)
        } else {
            timeView.setText(R.string.block_list_row_now)
        }

        hashView.text = formatHash(null, header.hashAsString, 8, 0, ' ')

        var iTransactionView = 0
        val transactionChildCount = transactionsViewGroup.childCount - ROW_BASE_CHILD_COUNT
        for (tx in transactions) {
            if (tx.appearsInHashes!!.containsKey(header.hash)) {
                val view: View
                if (iTransactionView < transactionChildCount) {
                    view = transactionsViewGroup.getChildAt(ROW_INSERT_INDEX + iTransactionView)
                } else {
                    val inflater = LayoutInflater.from(itemView.context)
                    view = inflater.inflate(R.layout.block_list_row_transaction, transactionsViewGroup, false)
                    transactionsViewGroup.addView(view, ROW_INSERT_INDEX + iTransactionView)
                }

                bindView(view, tx, wallet)
                iTransactionView++
            }
        }

        val leftoverTransactionViews = transactionChildCount - iTransactionView
        if (leftoverTransactionViews > 0) {
            transactionsViewGroup.removeViews(ROW_INSERT_INDEX + iTransactionView, leftoverTransactionViews)
        }

//        if (onClickListener != null) {
//            holder.menuView.setOnClickListener(View.OnClickListener { v -> onClickListener!!.onBlockMenuClick(v, storedBlock) })
//        }
    }

    private fun bindView(row: View, tx: Transaction, wallet: Wallet) {
        val isCoinBase = tx.isCoinBase
        val isInternal = tx.purpose == Transaction.Purpose.KEY_ROTATION

        val value = tx.getValue(wallet)
        val sent = value.signum() < 0
        val self = isEntirelySelf(tx, wallet)
        val address: Address?
        address = if (sent) getToAddressOfSent(tx, wallet) else getWalletAddressOfReceived(tx, wallet)

        // receiving or sending
        val rowFromTo = row.findViewById<View>(R.id.block_row_transaction_fromto) as TextView
        when {
            (isInternal || self) -> rowFromTo.setText(R.string.symbol_internal)
            sent -> rowFromTo.setText(R.string.symbol_to)
            else -> rowFromTo.setText(R.string.symbol_from)
        }

        // address
        val rowAddress = row.findViewById<TextView>(R.id.block_row_transaction_address)
        val label: String
        label = when {
            isCoinBase -> textCoinBase
            (isInternal || self) -> textInternal
            else -> "?"
        }
        rowAddress.text = label ?: address!!.toBase58()

        //        // value
        //        final CurrencyTextView rowValue = (CurrencyTextView) row.findViewById(R.id.block_row_transaction_value);
        //        rowValue.setAlwaysSigned(true);
        //        rowValue.setFormat(format);
        //        rowValue.setAmount(value);
    }

    private fun getWalletAddressOfReceived(tx: Transaction, wallet: Wallet): Address? {
        for (output in tx.outputs) {
            try {
                if (output.isMine(wallet)) {
                    val script = output.scriptPubKey
                    return script.getToAddress(wallet.networkParameters, true)
                }
            } catch (x: ScriptException) {
                // swallow
            }
        }
        return null
    }

    private fun getToAddressOfSent(tx: Transaction, wallet: Wallet): Address? {
        for (output in tx.outputs) {
            try {
                if (!output.isMine(wallet)) {
                    val script = output.scriptPubKey
                    return script.getToAddress(wallet.networkParameters, true)
                }
            } catch (x: ScriptException) {
                // swallow
            }
        }
        return null
    }

    private fun isEntirelySelf(tx: Transaction, wallet: Wallet): Boolean {
        for (input in tx.inputs) {
            val connectedOutput = input.connectedOutput
            if (connectedOutput == null || !connectedOutput.isMine(wallet))
                return false
        }

        for (output in tx.outputs) {
            if (!output.isMine(wallet))
                return false
        }

        return true
    }

    private fun isMiningRewardHalvingPoint(storedPrev: StoredBlock): Boolean {
        return (storedPrev.height + 1) % 210000 == 0
    }

    private fun formatHash(prefix: String?, address: String, groupSize: Int, lineSize: Int, groupSeparator: Char): Editable {
        val builder = if (prefix != null) SpannableStringBuilder(prefix) else SpannableStringBuilder()
        val len = address.length
        var i = 0
        while (i < len) {
            val end = i + groupSize
            val part = address.substring(i, if (end < len) end else len)

            builder.append(part)
            builder.setSpan(TypefaceSpan("monospace"), builder.length - part.length, builder.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            if (end < len) {
                val endOfLine = lineSize > 0 && end % lineSize == 0
                builder.append(if (endOfLine) '\n' else groupSeparator)
            }
            i += groupSize
        }

        return builder
    }

    private fun getString(resId: Int): String {
        return itemView.context.getString(resId)
    }
}
