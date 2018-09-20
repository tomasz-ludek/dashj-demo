package org.dash.dashj.demo.ui

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import org.dashj.dashjinterface.data.BlockchainStateLiveData

class BaseListViewModel(application: Application) : AndroidViewModel(application) {

    private val _blockchainState = BlockchainStateLiveData(application)
    val blockchainState
        get() = _blockchainState
}
