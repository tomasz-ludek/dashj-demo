package org.dash.dashj.demo.ui.governancelist

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import org.bitcoinj.governance.GovernanceObject

class GovernanceListViewModel : ViewModel() {

    private val _governanceList = GovernanceListLiveData()

    val governanceList: LiveData<List<GovernanceObject>>
        get() = _governanceList
}
