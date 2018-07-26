package org.dash.dashj.demo.ui.blocklist

import android.annotation.SuppressLint
import android.arch.lifecycle.MutableLiveData
import android.os.AsyncTask
import org.bitcoinj.core.Transaction
import org.bitcoinj.wallet.Wallet


class TransactionListLiveData : MutableLiveData<Set<Transaction>>() {

    val isLoaded get() = value != null

    fun loadTransactions(wallet: Wallet) {
        LoadTransactionsTask().execute(wallet)
    }

    @SuppressLint("StaticFieldLeak")
    internal inner class LoadTransactionsTask : AsyncTask<Wallet, Void, Set<Transaction>>() {

        override fun doInBackground(vararg args: Wallet): Set<Transaction> {
            val wallet = args[0]
//            org.bitcoinj.core.Context.propagate(Constants.CONTEXT)
            val transactionSet = wallet.getTransactions(true)
            val filteredTransactions = HashSet<Transaction>(transactionSet.size)
            for (tx in transactionSet) {
                val appearsIn = tx.appearsInHashes
                if (appearsIn != null && !appearsIn.isEmpty()) {
                    // TODO filter by updateTime
                    filteredTransactions.add(tx)
                }
            }
            return filteredTransactions
        }

        override fun onPostExecute(result: Set<Transaction>) {
            value = result
        }
    }
}