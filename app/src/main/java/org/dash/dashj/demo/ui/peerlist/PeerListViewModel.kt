package org.dash.dashj.demo.ui.peerlist

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import org.bitcoinj.core.Peer
import org.dash.dashj.demo.event.PeerListUpdateEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.net.InetAddress

class PeerListViewModel(application: Application) : AndroidViewModel(application) {

    private val _peerList = MutableLiveData<List<Peer>>()
    private val _hostNameMap = ReverseDnsLiveData()

    val peerList: LiveData<List<Peer>>
        get() = _peerList

    val hostNames: LiveData<Map<InetAddress, String>>
        get() = _hostNameMap

    init {
        EventBus.getDefault().register(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    fun onPeerListUpdateEvent(event: PeerListUpdateEvent) {
        _peerList.value = event.peerList
    }

    override fun onCleared() {
        super.onCleared()
        EventBus.getDefault().unregister(this)
    }
}
