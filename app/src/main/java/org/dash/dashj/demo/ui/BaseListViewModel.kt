package org.dash.dashj.demo.ui

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import org.dash.dashj.demo.event.SyncUpdateEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

class BaseListViewModel : ViewModel() {

    private val _pct = MutableLiveData<Double>()
    private var _blocksSoFar: Int? = null
    private var _date: Date? = null

    init {
        EventBus.getDefault().register(this)
    }

    val pct: LiveData<Double>
        get() = _pct

    val blocksSoFar: Int?
        get() = _blocksSoFar

    val date: Date?
        get() = _date

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN_ORDERED)
    fun onSyncUpdateEvent(event: SyncUpdateEvent) {
        _pct.value = event.pct
        _blocksSoFar = event.blocksSoFar
        _date = event.date
    }

    override fun onCleared() {
        super.onCleared()
        EventBus.getDefault().unregister(this)
    }
}
