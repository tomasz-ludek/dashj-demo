package org.dash.dashj.demo.ui.sporklist

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import org.dashj.dashjinterface.data.SporksLiveData

class SporkListViewModel(application: Application) : AndroidViewModel(application) {

    private val _sporkList = SporksLiveData(application)
    val sporkList: SporksLiveData
        get() = _sporkList
}
