package org.dash.dashj.demo.ui.masternodelist

import android.arch.lifecycle.MutableLiveData
import org.bitcoinj.core.Masternode
import org.bitcoinj.core.MasternodeManager
import org.dash.dashj.demo.WalletManager
import org.dash.dashj.demo.event.MasternodeListRequestEvent
import org.dash.dashj.demo.event.MasternodeListUpdateEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MasternodeListLiveData : MutableLiveData<List<Masternode>>() {

    private val masternodeCache = mutableListOf<Masternode>()

    private val masternodeManager: MasternodeManager = WalletManager.getInstance().wallet.context.masternodeManager

    override fun onActive() {
        EventBus.getDefault().register(this)
        EventBus.getDefault().post(MasternodeListRequestEvent())
    }

    override fun onInactive() {
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    fun onMasternodeListUpdateEvent(event: MasternodeListUpdateEvent) {
        value = masternodeManager.masternodes
    }
}