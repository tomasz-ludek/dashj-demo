package org.dash.dashj.demo.ui.sporklist

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import org.bitcoinj.core.SporkMessage

class SporkListViewModel : ViewModel() {

    private val _sporkList = SporkListLiveData()

    val sporkList: LiveData<List<SporkMessage>>
        get() = _sporkList
}
