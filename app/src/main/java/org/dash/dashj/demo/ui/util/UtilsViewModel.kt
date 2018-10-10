package org.dash.dashj.demo.ui.util

import android.app.Application
import org.dashj.dashjinterface.data.BlockchainStateLiveData
import org.dashj.dashjinterface.data.DjInterfaceViewModel
import org.dashj.dashjinterface.data.WalletInfoLiveData

class UtilsViewModel(application: Application) : DjInterfaceViewModel(application) {

    private val _walletInfo = WalletInfoLiveData(application)
    val walletInfo
        get() = _walletInfo

    private val _blockchainState = BlockchainStateLiveData(application)
    val blockchainState
        get() = _blockchainState

    fun walletToString(): String? {
        return djService.value?.wallet!!.toString(true, true, true, null)
    }
}
