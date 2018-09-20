package org.dash.dashj.demo.ui.util

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import org.dashj.dashjinterface.data.BlockchainStateLiveData
import org.dashj.dashjinterface.data.WalletInfoLiveData

class UtilsViewModel(application: Application) : AndroidViewModel(application) {

    private val _walletInfo = WalletInfoLiveData(application)
    val walletInfo
        get() = _walletInfo

    private val _blockchainState = BlockchainStateLiveData(application)
    val blockchainState
        get() = _blockchainState
}
