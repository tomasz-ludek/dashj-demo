package org.dash.dashj.demo.ui.masternodelist

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import org.bitcoinj.core.Masternode
import org.dashj.dashjinterface.data.MasternodesLiveData

class MasternodeListViewModel(application: Application) : AndroidViewModel(application) {

    private val _masternodeList = MasternodesLiveData(application)
    val masternodeList: LiveData<List<Masternode>>
        get() = _masternodeList
}
