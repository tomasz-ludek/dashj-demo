package org.dash.dashj.demo.ui.sporklist

import android.arch.lifecycle.MutableLiveData
import org.bitcoinj.core.SporkMessage
import org.dash.dashj.demo.WalletManager
import org.dash.dashj.demo.event.SporkListRequestEvent
import org.dash.dashj.demo.event.SporkListUpdateEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class SporkListLiveData : MutableLiveData<List<SporkMessage>>() {

    private val sporkCache = mutableListOf<SporkMessage>()

    override fun onActive() {
        EventBus.getDefault().register(this)
        EventBus.getDefault().post(SporkListRequestEvent())
    }

    override fun onInactive() {
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    fun onSporkListUpdateEvent(event: SporkListUpdateEvent) {
        val sporks = WalletManager.getInstance().wallet.context.
                sporkManager.sporks.sortedWith(compareBy { it.sporkID })
        sporkCache.clear()
        sporkCache.addAll(sporks)

        value = sporkCache
    }
}