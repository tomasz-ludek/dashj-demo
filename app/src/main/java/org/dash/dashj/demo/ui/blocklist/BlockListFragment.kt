package org.dash.dashj.demo.ui.blocklist

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.block_list_fragment.view.*
import org.dash.dashj.demo.R
import org.dash.dashj.demo.WalletManager
import org.dash.dashj.demo.ui.adapter.BlockListAdapter


class BlockListFragment : Fragment() {

    companion object {
        private const val NO_PEERS_PLACEHOLDER_VIEW = 0
        private const val PEERS_LIST_VIEW = 1

        fun newInstance() = BlockListFragment()
    }

    private lateinit var viewModel: BlockListViewModel
    private lateinit var adapter: BlockListAdapter
    private lateinit var layoutView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        layoutView = inflater.inflate(R.layout.block_list_fragment, container, false)
        initPeerRecyclerView()
        return layoutView
    }

    private fun initPeerRecyclerView() {
        layoutView.blockRecyclerView.layoutManager = LinearLayoutManager(context)
        layoutView.blockRecyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        adapter = BlockListAdapter(activity, WalletManager.getInstance().wallet, null)
        layoutView.blockRecyclerView.adapter = adapter
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bindViewModel()
    }

    private fun bindViewModel() {
        viewModel = ViewModelProviders.of(this).get(BlockListViewModel::class.java)

        viewModel.blockList.observe(this, Observer { blockList ->
            adapter.replace(blockList)
            layoutView.rootAnimatorView.displayedChild =
                    if (blockList != null) PEERS_LIST_VIEW else NO_PEERS_PLACEHOLDER_VIEW
        })

        viewModel.transactionSet.observe(this, Observer { transactionSet ->
            adapter.replaceTransactions(transactionSet)
        })
    }

}
