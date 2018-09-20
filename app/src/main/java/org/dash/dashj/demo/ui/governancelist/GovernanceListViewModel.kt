package org.dash.dashj.demo.ui.governancelist

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import org.dashj.dashjinterface.data.GovernanceLiveData

class GovernanceListViewModel(application: Application) : AndroidViewModel(application) {

    private val _governanceList = GovernanceLiveData(application)
    val governanceList: GovernanceLiveData
        get() = _governanceList
}
