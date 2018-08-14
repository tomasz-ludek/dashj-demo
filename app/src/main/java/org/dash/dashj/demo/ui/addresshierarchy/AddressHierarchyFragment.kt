package org.dash.dashj.demo.ui.addresshierarchy

import android.arch.lifecycle.Observer
import org.dash.dashj.demo.R
import org.dash.dashj.demo.WalletManager
import org.dash.dashj.demo.event.AddressesHierarchyRequestEvent
import org.dash.dashj.demo.ui.BaseListFragment
import org.greenrobot.eventbus.EventBus


class AddressHierarchyFragment : BaseListFragment<AddressHierarchyAdapter, AddressHierarchyViewModel>() {

    override val emptyStateMessageResId: Int
        get() = R.string.default_empty_state_message

    companion object {
        fun newInstance() = AddressHierarchyFragment()
    }

    override fun viewModelType(): Class<AddressHierarchyViewModel> = AddressHierarchyViewModel::class.java

    override fun createAdapter(): AddressHierarchyAdapter {
        return AddressHierarchyAdapter(activity, WalletManager.getInstance().wallet)
    }

    override fun initView() {
        super.initView()
        activity!!.setTitle(R.string.fragment_address_hierarchy_title)
    }

    override fun bindViewModel(viewModel: AddressHierarchyViewModel) {
        viewModel.keyList.observe(this, Observer { keyList ->
            keyList?.let {
                adapter.replace(keyList)
                updateView(it.isNotEmpty())
                setInfo("Addresses count: ${it.size}")
            }
        })
    }

    override fun onRefresh() {
        EventBus.getDefault().post(AddressesHierarchyRequestEvent())
    }
}
