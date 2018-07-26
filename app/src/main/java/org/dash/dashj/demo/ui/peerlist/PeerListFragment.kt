package org.dash.dashj.demo.ui.peerlist

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.peer_list_fragment.view.*
import org.dash.dashj.demo.R
import org.dash.dashj.demo.ui.adapter.PeerListAdapter

class PeerListFragment : Fragment() {

    private val NO_PEERS_PLACEHOLDER_VIEW = 0
    private val PEERS_LIST_VIEW = 1

    private lateinit var viewModel: PeerListViewModel

    companion object {
        fun newInstance() = PeerListFragment()
    }

    private lateinit var adapter: PeerListAdapter
    private lateinit var layoutView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        layoutView = inflater.inflate(R.layout.peer_list_fragment, container, false)
        initPeerRecyclerView()
        return layoutView
    }

    private fun initPeerRecyclerView() {
        layoutView.peerRecyclerView.layoutManager = LinearLayoutManager(context)
        layoutView.peerRecyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        adapter = PeerListAdapter(context!!)
        layoutView.peerRecyclerView.adapter = adapter
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bindViewModel()
    }

    private fun bindViewModel() {
        viewModel = ViewModelProviders.of(this).get(PeerListViewModel::class.java)

        viewModel.peerList.observe(this, Observer { peerList ->
            adapter.replace(peerList)
            layoutView.rootAnimatorView.displayedChild =
                    if (peerList != null) PEERS_LIST_VIEW else NO_PEERS_PLACEHOLDER_VIEW
        })

        viewModel.hostNames.observe(this, Observer { hostNameMap ->
            adapter.updateHostNameCache(hostNameMap!!)
        })
    }

}
