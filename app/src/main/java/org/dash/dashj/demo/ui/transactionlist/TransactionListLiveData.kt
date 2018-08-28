package org.dash.dashj.demo.ui.transactionlist

import android.arch.lifecycle.MutableLiveData
import org.bitcoinj.core.Transaction
import org.dash.dashj.demo.WalletManager
import org.dash.dashj.demo.event.BlockListUpdateEvent
import org.dash.dashj.demo.event.TransactionListRequestEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class TransactionListLiveData : MutableLiveData<List<Transaction>>() {

    private val walletManager = WalletManager.getInstance()

    override fun onActive() {
        EventBus.getDefault().register(this)
        updateValue()
    }

    override fun onInactive() {
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    fun onBlockListUpdateEvent(event: BlockListUpdateEvent) {
        updateValue()
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    fun onTransactionListRequestEvent(event: TransactionListRequestEvent) {
        updateValue()
    }

    private fun updateValue() {
        value = walletManager.wallet.transactionsByTime
    }
}
