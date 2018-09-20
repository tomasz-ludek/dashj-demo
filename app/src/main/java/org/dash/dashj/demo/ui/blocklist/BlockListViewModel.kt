package org.dash.dashj.demo.ui.blocklist

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import org.bitcoinj.core.Transaction
import org.dash.dashj.demo.WalletManager
import org.dashj.dashjinterface.data.BlockchainSyncLiveData

class BlockListViewModel(application: Application) : AndroidViewModel(application) {

    private val _blockList = BlockchainSyncLiveData(application)
    val blockList: BlockchainSyncLiveData
        get() = _blockList

    private val _transactionSet = TransactionListLiveData()
    val transactionSet: LiveData<Set<Transaction>>
        get() {
            if (!_transactionSet.isLoaded) {
                _transactionSet.loadTransactions(WalletManager.getInstance().wallet)
            }
            return _transactionSet
        }
}
