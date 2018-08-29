package org.dash.dashj.demo.ui.masternodelist

import android.annotation.SuppressLint
import android.arch.lifecycle.MutableLiveData
import android.os.AsyncTask
import org.bitcoinj.core.Masternode
import org.bitcoinj.core.MasternodeManager
import org.dash.dashj.demo.WalletManager
import org.dash.dashj.demo.event.MasternodeListUpdateEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class MasternodeListLiveData : MutableLiveData<List<Masternode>>() {

    private val masternodeCache = mutableListOf<Masternode>()

    private val masternodeManager: MasternodeManager = WalletManager.getInstance().wallet.context.masternodeManager

    private var loadingTask: LoadMasternodesTask? = null

    private var _latestSyncStatus = -1

    public val latestSyncStatus
        get() = _latestSyncStatus

    override fun onActive() {
        EventBus.getDefault().register(this)
        updateValue()
    }

    override fun onInactive() {
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    fun onMasternodeListUpdateEvent(event: MasternodeListUpdateEvent) {
        _latestSyncStatus = event.syncStatus
        updateValue()
    }

    private fun updateValue() {
        if (loadingTask == null) {
            loadingTask = LoadMasternodesTask()
            loadingTask?.execute(masternodeManager)
        }
    }

    @SuppressLint("StaticFieldLeak")
    internal inner class LoadMasternodesTask : AsyncTask<MasternodeManager, Void, List<Masternode>>() {

        override fun doInBackground(vararg args: MasternodeManager): List<Masternode> {
            val wallet = args[0]
//            org.bitcoinj.core.Context.propagate(Constants.CONTEXT)
            return masternodeManager.masternodes
        }

        override fun onPostExecute(result: List<Masternode>) {
            masternodeCache.clear()
            masternodeCache.addAll(masternodeManager.masternodes)
            value = masternodeCache
            loadingTask = null
        }
    }
}
