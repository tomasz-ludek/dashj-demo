package org.dash.dashj.demo.ui.blocklist

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import org.bitcoinj.core.StoredBlock
import org.bitcoinj.core.Transaction
import org.dash.dashj.demo.WalletManager

class BlockListViewModel : ViewModel() {

    private val _blockList = BlockListLiveData()
    private val _transactionSet = TransactionListLiveData()

    val blockList: LiveData<List<StoredBlock>>
        get() = _blockList

    val transactionSet: LiveData<Set<Transaction>>
        get() {
            if (!_transactionSet.isLoaded) {
                _transactionSet.loadTransactions(WalletManager.getInstance().wallet)
            }
            return _transactionSet
        }
}
