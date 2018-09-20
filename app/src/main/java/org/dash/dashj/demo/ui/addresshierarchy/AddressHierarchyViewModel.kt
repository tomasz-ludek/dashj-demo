package org.dash.dashj.demo.ui.addresshierarchy

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import org.dashj.dashjinterface.data.KeyChainLiveData

class AddressHierarchyViewModel(application: Application) : AndroidViewModel(application) {

    private val _keyChain = KeyChainLiveData(application)
    val keyChain: KeyChainLiveData
        get() = _keyChain
}
