package org.dash.dashj.demo.ui.transactionlist

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import org.bitcoinj.core.Transaction

class TransactionListViewModel : ViewModel() {

    private val _transactionList = TransactionListLiveData()

    val transactionList: LiveData<List<Transaction>>
        get() = _transactionList
}
