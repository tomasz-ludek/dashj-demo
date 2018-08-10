package org.dash.dashj.demo.ui.masternodelist

import android.arch.lifecycle.MutableLiveData
import org.bitcoinj.core.Masternode
import org.bitcoinj.core.MasternodeManager
import org.dash.dashj.demo.WalletManager
import org.dash.dashj.demo.event.MasternodeListUpdateEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class MasternodeListLiveData : MutableLiveData<List<Masternode>>() {

    private val masternodeCache = mutableListOf<Masternode>()

    private val masternodeManager: MasternodeManager = WalletManager.getInstance().wallet.context.masternodeManager

    override fun onActive() {
        EventBus.getDefault().register(this)
        updateValue()
    }

    override fun onInactive() {
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    fun onMasternodeListUpdateEvent(event: MasternodeListUpdateEvent) {
        updateValue()
    }

    fun updateValue() {
        masternodeCache.clear()
        masternodeCache.addAll(masternodeManager.masternodes)
//        val masternodeAddress = MasternodeAddress(InetAddress.getByName("109.235.67.212"), 9999)
//        val mn = Context.get().masternodeManager.find(masternodeAddress)
//        if (mn != null) {
//            masternodeCache.add(mn)
//        }
//        value = masternodeCache
        value = masternodeCache
    }
}
