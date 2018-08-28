package org.dash.dashj.demo.ui.transactionlist

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import org.bitcoinj.core.Transaction
import org.dash.dashj.demo.R
import org.dash.dashj.demo.WalletManager
import java.util.*

class TransactionListAdapter(context: Context) : RecyclerView.Adapter<TransactionViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private val transactions = LinkedList<Transaction>()
    private val walletManager = WalletManager.getInstance()

    init {
        setHasStableIds(true)
    }

    fun clear() {
        transactions.clear()
        notifyDataSetChanged()
    }

    fun replace(transactions: List<Transaction>?) {
        this.transactions.clear()
        transactions?.let { this.transactions.addAll(it) }

        notifyDataSetChanged()
    }

    private fun getItem(position: Int): Transaction {
        return transactions[position]
    }

    override fun getItemCount(): Int {
        return transactions.size
    }

    override fun getItemId(position: Int): Long {
        return transactions[position].hash.toBigInteger().toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val itemView = inflater.inflate(R.layout.transaction_list_row, parent, false)
        return TransactionViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = getItem(position)
        holder.bind(transaction, walletManager.wallet)
    }
}