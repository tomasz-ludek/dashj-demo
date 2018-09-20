package org.dash.dashj.demo.ui.transactionlist

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import org.dashj.dashjinterface.data.TransactionsLiveData

class TransactionListViewModel(application: Application) : AndroidViewModel(application) {

    private val _transactions = TransactionsLiveData(application)
    val transactions: TransactionsLiveData
        get() = _transactions
}
