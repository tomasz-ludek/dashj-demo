package org.dash.dashj.demo.ui.peerlist

import android.arch.lifecycle.Observer
import org.dash.dashj.demo.R
import org.dash.dashj.demo.ui.BaseListFragment

class PeerListFragment : BaseListFragment<PeerListAdapter, PeerListViewModel>() {

    override val progressMessageResId: Int
        get() = R.string.connecting_state_message

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
        viewModel.peerConnectivity.observe(this, Observer {
            adapter.replace(it)
            viewModel.hostNames.fire(it)
            updateView(it!!.isNotEmpty())
            setInfo("Peers count: ${it.size}")
        })

        viewModel.hostNames.observe(this, Observer { hostNameMap ->
            adapter.updateHostNameCache(hostNameMap!!)
        })
    }

    override fun onRefresh() {

    }
}
