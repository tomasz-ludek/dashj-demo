package org.dash.dashj.demo.ui.peerlist

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import org.bitcoinj.core.Peer
import java.net.InetAddress

class PeerListViewModel : ViewModel() {

    private val _peerList = PeerListLiveData()
    private val _hostNameMap = ReverseDnsLiveData()

    val peerList: LiveData<List<Peer>>
        get() = _peerList

    val hostNames: LiveData<Map<InetAddress, String>>
        get() = _hostNameMap
}
