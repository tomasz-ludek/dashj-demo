package org.dash.dashj.demo.ui.addresshierarchy

import android.arch.lifecycle.MutableLiveData
import org.bitcoinj.core.ECKey
import org.dash.dashj.demo.WalletManager
import org.dash.dashj.demo.event.AddressesHierarchyRequestEvent
import org.dash.dashj.demo.util.RestrictedAccessUtil
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class AddressHierarchyLiveData : MutableLiveData<List<ECKey>>() {

    private val addressCache = mutableListOf<ECKey>()

    private val walletManager = WalletManager.getInstance()

    override fun onActive() {
        EventBus.getDefault().register(this)
        updateValue()
    }

    override fun onInactive() {
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    fun onBlockListUpdateEvent(event: AddressesHierarchyRequestEvent) {
        updateValue()
    }

    private fun updateValue() {
        addressCache.clear()
        val keyChainGroup = walletManager.wallet.activeKeyChain
        val keyList = RestrictedAccessUtil.invokeGetKeys(keyChainGroup, false)
        addressCache.addAll(keyList)
        val importedKeys = walletManager.wallet.importedKeys
        addressCache.addAll(importedKeys)
//        val chainAsString = keyChainGroup.toString(false, walletManager.wallet.networkParameters)
        value = addressCache
    }
}