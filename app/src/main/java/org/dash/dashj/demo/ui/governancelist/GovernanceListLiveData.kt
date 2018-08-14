package org.dash.dashj.demo.ui.governancelist

import android.arch.lifecycle.MutableLiveData
import org.bitcoinj.governance.GovernanceObject
import org.dash.dashj.demo.WalletManager
import org.dash.dashj.demo.event.GovernanceObjectsUpdateEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class GovernanceListLiveData : MutableLiveData<List<GovernanceObject>>() {

    private val governanceObjectsCache = mutableListOf<GovernanceObject>()

    private val governanceManager = WalletManager.getInstance().wallet.context.governanceManager

    override fun onActive() {
        EventBus.getDefault().register(this)
        updateValue()
    }

    override fun onInactive() {
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    fun onGovernanceObjectsUpdateEvent(event: GovernanceObjectsUpdateEvent) {
        updateValue()
    }

    private fun updateValue() {
        governanceObjectsCache.clear()
        governanceObjectsCache.addAll(governanceManager.getAllNewerThan(0))
        value = governanceObjectsCache
    }
}
