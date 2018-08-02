package org.dash.dashj.demo.ui.masternodelist

import android.arch.lifecycle.Observer
import org.dash.dashj.demo.R
import org.dash.dashj.demo.WalletManager
import org.dash.dashj.demo.event.MasternodeListRequestEvent
import org.dash.dashj.demo.ui.BaseListFragment
import org.greenrobot.eventbus.EventBus

class MasternodeListFragment : BaseListFragment<MasternodeListAdapter, MasternodeListViewModel>() {

    override val emptyStateMessageResId: Int
        get() = R.string.default_empty_state_message

    companion object {
        fun newInstance() = MasternodeListFragment()
    }

    override fun viewModelType(): Class<MasternodeListViewModel> = MasternodeListViewModel::class.java

    override fun createAdapter(): MasternodeListAdapter {
        val masternodeManager = WalletManager.getInstance().wallet.context.masternodeManager
        return MasternodeListAdapter(context!!, masternodeManager)
    }

    override fun onResume() {
        super.onResume()
        onRefresh()
    }

    override fun initView() {
        super.initView()
        activity!!.setTitle(R.string.fragment_masternode_list_title)
    }

    override fun bindViewModel(viewModel: MasternodeListViewModel) {
        viewModel.sporkList.observe(this, Observer { masternodeList ->
            adapter.replace(masternodeList)
            updateView(masternodeList != null)
        })
    }

    override fun onRefresh() {
        EventBus.getDefault().post(MasternodeListRequestEvent())
    }
}
