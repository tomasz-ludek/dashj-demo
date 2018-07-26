package org.dash.dashj.demo.ui.blocklist

import android.arch.lifecycle.MutableLiveData
import org.bitcoinj.core.StoredBlock
import org.dash.dashj.demo.event.BlockListRequestEvent
import org.dash.dashj.demo.event.BlockListUpdateEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class BlockListLiveData : MutableLiveData<List<StoredBlock>>() {

    private val blockCache = mutableListOf<StoredBlock>()

    override fun onActive() {
        EventBus.getDefault().register(this)
        EventBus.getDefault().post(BlockListRequestEvent())
    }

    override fun onInactive() {
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    fun onBlockListUpdateEvent(event: BlockListUpdateEvent) {
        blockCache.clear()
        event.blockList.let {
            blockCache.addAll(it)
            value = blockCache
        }
    }
}