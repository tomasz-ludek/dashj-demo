package org.dash.dashj.demo.ui.sporklist

import android.arch.lifecycle.Observer
import org.dash.dashj.demo.R
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
        return SporkListAdapter(context!!)
    }

    override fun initView() {
        super.initView()
        activity!!.setTitle(R.string.fragment_spork_list_title)
    }

    override fun bindViewModel(viewModel: SporkListViewModel) {
        viewModel.sporkList.observe(this, Observer {
            val sporks = it!!.sporks
            val active = it.active
            adapter.replace(sporks, active)
            updateView(sporks.isNotEmpty())
            setInfo("Sporks count: ${sporks.size}")
        })
    }

    override fun onRefresh() {
        EventBus.getDefault().post(SporkListRequestEvent())
    }
}
