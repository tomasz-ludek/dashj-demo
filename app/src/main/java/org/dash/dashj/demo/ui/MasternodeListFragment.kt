package org.dash.dashj.demo.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.widget.RecyclerView
import org.dash.dashj.demo.R
import org.dash.dashj.demo.WalletManager
import org.dash.dashj.demo.event.MasternodeListRequestEvent
import org.dash.dashj.demo.ui.masternodelist.MasternodeListAdapter
import org.dash.dashj.demo.ui.masternodelist.MasternodeListViewModel
import org.greenrobot.eventbus.EventBus

class MasternodeListFragment : BaseListFragment() {

    override val emptyStateMessageResId: Int
        get() = R.string.default_empty_state_message

    companion object {
        fun newInstance() = MasternodeListFragment()
    }

    private lateinit var _adapter: MasternodeListAdapter

    override val adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>
        get() {
            if (!this::_adapter.isInitialized) {
                val masternodeManager = WalletManager.getInstance().wallet.context.masternodeManager
                _adapter = MasternodeListAdapter(context!!, masternodeManager)
            }
            return _adapter
        }

    private lateinit var viewModel: MasternodeListViewModel

    override fun bindViewModel() {
        viewModel = ViewModelProviders.of(this).get(MasternodeListViewModel::class.java)

        viewModel.sporkList.observe(this, Observer { masternodeList ->
            _adapter.replace(masternodeList)
            updateView(masternodeList != null)
        })
    }

    override fun onRefresh() {
        EventBus.getDefault().post(MasternodeListRequestEvent())
    }
}
