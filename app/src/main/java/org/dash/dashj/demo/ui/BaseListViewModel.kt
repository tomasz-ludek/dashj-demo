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
    private val _blocksSoFar = MutableLiveData<Int>()
    private val _date = MutableLiveData<Date>()

    init {
        EventBus.getDefault().register(this)
    }

    val pct: LiveData<Double>
        get() = _pct

    val blocksSoFar: LiveData<Int>
        get() = _blocksSoFar

    val date: LiveData<Date>
        get() = _date

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN_ORDERED)
    fun onSyncUpdateEvent(event: SyncUpdateEvent) {
        _pct.value = event.pct
        _blocksSoFar.value = event.blocksSoFar
        _date.value = event.date
    }

    override fun onCleared() {
        super.onCleared()
        EventBus.getDefault().unregister(this)
    }
}
