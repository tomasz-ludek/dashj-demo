package org.dash.dashj.demo.ui.main

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.os.Handler
import org.bitcoinj.core.Peer
import org.dash.dashj.demo.event.PeerListUpdateEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MainViewModel : ViewModel() {

    private val _data = MutableLiveData<String>()
    private val _peerList = MutableLiveData<List<Peer>>()

    val data: LiveData<String>
        get() = _data

    val peerList: LiveData<List<Peer>>
        get() = _peerList

    init {
        _data.value = "Hello, world!"
        EventBus.getDefault().register(this)
        Handler().postDelayed({ _data.value = "Dupa Jasiu" }, 2000)
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    fun onPeerListRequestEvent(event: PeerListUpdateEvent) {
        _peerList.value = event.peerList
    }

    override fun onCleared() {
        super.onCleared()
        EventBus.getDefault().unregister(this)
    }
}
