package org.dash.dashj.demo.ui.peerlist

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import org.dashj.dashjinterface.data.PeerConnectivityLiveData

class PeerListViewModel(application: Application) : AndroidViewModel(application) {

    private val _peerConnectivity = PeerConnectivityLiveData(application)
    val peerConnectivity: PeerConnectivityLiveData
        get() = _peerConnectivity

    private val _hostNameMap = ReverseDnsLiveData()
    val hostNames: ReverseDnsLiveData
        get() = _hostNameMap
}
