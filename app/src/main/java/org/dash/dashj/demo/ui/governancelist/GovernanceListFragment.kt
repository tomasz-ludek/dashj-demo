package org.dash.dashj.demo.ui.governancelist

import android.arch.lifecycle.Observer
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Filterable
import org.dash.dashj.demo.R
import org.dash.dashj.demo.ui.BaseListFragment

class GovernanceListFragment : BaseListFragment<GovernanceListAdapter, GovernanceListViewModel>() {

    private lateinit var searchMenuItem: MenuItem

    override val progressMessageResId: Int
        get() = R.string.default_empty_state_message

    companion object {
        fun newInstance() = GovernanceListFragment()
    }

    override fun viewModelType(): Class<GovernanceListViewModel> = GovernanceListViewModel::class.java

    override fun createAdapter(): GovernanceListAdapter {
        return GovernanceListAdapter(context!!)
    }

    override fun onResume() {
        super.onResume()
        onRefresh()
    }

    override fun initView() {
        super.initView()
        activity!!.setTitle(R.string.fragment_governance_list_title)
        setHasOptionsMenu(true)
    }

    override fun bindViewModel(viewModel: GovernanceListViewModel) {
        viewModel.governanceList.observe(this, Observer { governanceList ->
            governanceList?.let {
                adapter.replace(it)
                updateView(it.isNotEmpty())
                setInfo("Objects count: ${it.size}")
            }
        })
    }

    override fun onRefresh() {
        if (::searchMenuItem.isInitialized) {
            searchMenuItem.collapseActionView()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.masternode_list, menu)
        searchMenuItem = menu!!.findItem(R.id.action_search)
        setupSearchView()
    }

    private fun setupSearchView() {
        val activity = activity as AppCompatActivity?
        val themedContext = activity!!.supportActionBar!!.themedContext
        val searchView = SearchView(themedContext)
        searchMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW or MenuItem.SHOW_AS_ACTION_IF_ROOM)
        searchMenuItem.actionView = searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                (adapter as Filterable).filter.filter(newText)
                return false
            }
        })
        searchView.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewDetachedFromWindow(v: View?) {
//                inSearchMode = false
            }

            override fun onViewAttachedToWindow(v: View?) {
//                inSearchMode = true
            }
        })
    }
}
