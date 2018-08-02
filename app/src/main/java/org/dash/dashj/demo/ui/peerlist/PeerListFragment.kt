package org.dash.dashj.demo.ui.peerlist

import android.arch.lifecycle.Observer
import org.dash.dashj.demo.R
import org.dash.dashj.demo.event.PeerListRequestEvent
import org.dash.dashj.demo.ui.BaseListFragment
import org.greenrobot.eventbus.EventBus

class PeerListFragment : BaseListFragment<PeerListAdapter, PeerListViewModel>() {

    override val emptyStateMessageResId: Int
        get() = R.string.default_empty_state_message

    companion object {
        fun newInstance() = PeerListFragment()
    }

    override fun viewModelType(): Class<PeerListViewModel> = PeerListViewModel::class.java

    override fun createAdapter(): PeerListAdapter {
        return PeerListAdapter(context!!)
    }

    override fun initView() {
        super.initView()
        activity!!.setTitle(R.string.fragment_peer_list_title)
    }

    override fun bindViewModel(viewModel: PeerListViewModel) {
        viewModel.peerList.observe(this, Observer { peerList ->
            adapter.replace(peerList)
            updateView(peerList != null)
        })

        viewModel.hostNames.observe(this, Observer { hostNameMap ->
            adapter.updateHostNameCache(hostNameMap!!)
        })
    }

    override fun onRefresh() {
        EventBus.getDefault().post(PeerListRequestEvent())
    }
}
