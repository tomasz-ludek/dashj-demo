package org.dash.dashj.demo.ui.blocklist

import android.arch.lifecycle.Observer
import org.dash.dashj.demo.R
import org.dash.dashj.demo.WalletManager
import org.dash.dashj.demo.event.BlockListRequestEvent
import org.dash.dashj.demo.ui.BaseListFragment
import org.greenrobot.eventbus.EventBus


class BlockListFragment : BaseListFragment<BlockListAdapter, BlockListViewModel>() {

    override val emptyStateMessageResId: Int
        get() = R.string.default_empty_state_message

    companion object {
        fun newInstance() = BlockListFragment()
    }

    override fun viewModelType(): Class<BlockListViewModel> = BlockListViewModel::class.java

    override fun createAdapter(): BlockListAdapter {
        return BlockListAdapter(activity, WalletManager.getInstance().wallet, null)
    }

    override fun initView() {
        super.initView()
        activity!!.setTitle(R.string.fragment_block_list_title)
    }

    override fun bindViewModel(viewModel: BlockListViewModel) {
        viewModel.blockList.observe(this, Observer { blockList ->
            adapter.replace(blockList)
            updateView(blockList != null)
        })

        viewModel.transactionSet.observe(this, Observer { transactionSet ->
            adapter.replaceTransactions(transactionSet)
        })
    }

    override fun onRefresh() {
        EventBus.getDefault().post(BlockListRequestEvent())
    }
}
