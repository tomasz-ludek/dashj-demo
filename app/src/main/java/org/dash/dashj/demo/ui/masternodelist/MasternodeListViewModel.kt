package org.dash.dashj.demo.ui.masternodelist

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import org.bitcoinj.core.Masternode

class MasternodeListViewModel : ViewModel() {

    private val _masternodeList = MasternodeListLiveData()

    val sporkList: LiveData<List<Masternode>>
        get() = _masternodeList
}
