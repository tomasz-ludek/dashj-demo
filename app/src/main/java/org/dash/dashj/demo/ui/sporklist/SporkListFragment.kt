package org.dash.dashj.demo.ui.sporklist

import android.arch.lifecycle.Observer
import org.dash.dashj.demo.R
import org.dash.dashj.demo.WalletManager
import org.dash.dashj.demo.event.SporkListRequestEvent
import org.dash.dashj.demo.ui.BaseListFragment
import org.greenrobot.eventbus.EventBus

class SporkListFragment : BaseListFragment<SporkListAdapter, SporkListViewModel>() {

    override val emptyStateMessageResId: Int
        get() = R.string.default_empty_state_message

    companion object {
        fun newInstance() = SporkListFragment()
    }

    override fun viewModelType(): Class<SporkListViewModel> = SporkListViewModel::class.java

    override fun createAdapter(): SporkListAdapter {
        val sporkManager = WalletManager.getInstance().wallet.context.sporkManager
        return SporkListAdapter(context!!, sporkManager)
    }

    override fun bindViewModel(viewModel: SporkListViewModel) {
        viewModel.sporkList.observe(this, Observer { sporkList ->
            adapter.replace(sporkList)
            updateView(sporkList != null)
        })
    }

    override fun onRefresh() {
        EventBus.getDefault().post(SporkListRequestEvent())
    }
}
