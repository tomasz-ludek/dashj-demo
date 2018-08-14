package org.dash.dashj.demo.ui.addresshierarchy

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import org.bitcoinj.core.ECKey

class AddressHierarchyViewModel : ViewModel() {

    private val _kayList = AddressHierarchyLiveData()

    val keyList: LiveData<List<ECKey>>
        get() = _kayList
}
