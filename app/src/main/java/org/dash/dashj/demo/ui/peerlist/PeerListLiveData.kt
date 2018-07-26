package org.dash.dashj.demo.ui.peerlist

import android.arch.lifecycle.MutableLiveData
import org.bitcoinj.core.Peer
import org.dash.dashj.demo.event.PeerListRequestEvent
import org.dash.dashj.demo.event.PeerListUpdateEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class PeerListLiveData : MutableLiveData<List<Peer>>() {

    private val peerCache = mutableListOf<Peer>()

    override fun onActive() {
        EventBus.getDefault().register(this)
        EventBus.getDefault().post(PeerListRequestEvent())
    }

    override fun onInactive() {
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    fun onPeerListUpdateEvent(event: PeerListUpdateEvent) {
        peerCache.clear()
        event.peerList.let {
            peerCache.addAll(it)
            value = peerCache
        }
    }
}