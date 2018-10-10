package org.dash.dashj.demo.ui.blocklist

import android.app.Application
import android.arch.lifecycle.LiveData
import org.bitcoinj.core.Transaction
import org.dashj.dashjinterface.data.BlockchainSyncLiveData
import org.dashj.dashjinterface.data.DjInterfaceViewModel

class BlockListViewModel(application: Application) : DjInterfaceViewModel(application) {

    private val _blockList = BlockchainSyncLiveData(application)
    val blockList: BlockchainSyncLiveData
        get() = _blockList

    private val _transactionSet = TransactionListLiveData()
    val transactionSet: LiveData<Set<Transaction>>
        get() {
            if (!_transactionSet.isLoaded) {
                _transactionSet.loadTransactions(djService.value!!.wallet)
            }
            return _transactionSet
        }
}
