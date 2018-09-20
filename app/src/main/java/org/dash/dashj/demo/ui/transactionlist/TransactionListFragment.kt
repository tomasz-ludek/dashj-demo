package org.dash.dashj.demo.ui.transactionlist

import android.arch.lifecycle.Observer
import org.dash.dashj.demo.R
import org.dash.dashj.demo.event.TransactionListRequestEvent
import org.dash.dashj.demo.ui.BaseListFragment
import org.greenrobot.eventbus.EventBus

class TransactionListFragment : BaseListFragment<TransactionListAdapter, TransactionListViewModel>() {

    override val emptyStateMessageResId: Int
        get() = R.string.loading_state_message

    companion object {
        fun newInstance() = TransactionListFragment()
    }

    override fun viewModelType(): Class<TransactionListViewModel> = TransactionListViewModel::class.java

    override fun createAdapter(): TransactionListAdapter {
        return TransactionListAdapter(context!!)
    }

    override fun initView() {
        super.initView()
        activity!!.setTitle(R.string.fragment_transaction_list_title)
    }

    override fun bindViewModel(viewModel: TransactionListViewModel) {
        viewModel.transactions.observe(this, Observer {
            adapter.replace(it!!.transactions, it.wallet)
            updateView(true)
        })
    }

    override fun onRefresh() {
        EventBus.getDefault().post(TransactionListRequestEvent())
    }
}
