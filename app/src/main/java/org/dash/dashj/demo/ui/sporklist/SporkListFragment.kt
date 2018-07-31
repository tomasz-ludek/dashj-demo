package org.dash.dashj.demo.ui.sporklist

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.spork_list_fragment.view.*
import org.dash.dashj.demo.R
import org.dash.dashj.demo.WalletManager
import org.dash.dashj.demo.event.SporkListRequestEvent
import org.dash.dashj.demo.ui.adapter.SporkListAdapter
import org.greenrobot.eventbus.EventBus

class SporkListFragment : Fragment() {

    private val NO_PEERS_PLACEHOLDER_VIEW = 0
    private val PEERS_LIST_VIEW = 1

    private lateinit var viewModel: SporkListViewModel

    companion object {
        fun newInstance() = SporkListFragment()
    }

    private lateinit var adapter: SporkListAdapter
    private lateinit var layoutView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        layoutView = inflater.inflate(R.layout.spork_list_fragment, container, false)
        initRecyclerView()
        return layoutView
    }

    private fun initRecyclerView() {
        layoutView.sporkRecyclerView.layoutManager = LinearLayoutManager(context)
        layoutView.sporkRecyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        adapter = SporkListAdapter(context!!, WalletManager.getInstance().wallet.context.sporkManager)
        layoutView.sporkRecyclerView.adapter = adapter

        layoutView.sporkRefreshView.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark)
        layoutView.sporkRefreshView.canChildScrollUp()
        layoutView.sporkRefreshView.setOnRefreshListener {
            EventBus.getDefault().post(SporkListRequestEvent())
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bindViewModel()
    }

    private fun bindViewModel() {
        viewModel = ViewModelProviders.of(this).get(SporkListViewModel::class.java)

        viewModel.sporkList.observe(this, Observer { sporkList ->
            adapter.replace(sporkList)
            updateView(sporkList != null)
        })
    }

    private fun updateView(sporkListVisible: Boolean) {
        layoutView.sporkRefreshView.isRefreshing = false
        layoutView.rootAnimatorView.displayedChild =
                if (sporkListVisible) PEERS_LIST_VIEW else NO_PEERS_PLACEHOLDER_VIEW
    }
}
