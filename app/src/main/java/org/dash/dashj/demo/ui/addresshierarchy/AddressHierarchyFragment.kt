package org.dash.dashj.demo.ui.addresshierarchy

import android.arch.lifecycle.Observer
import org.dash.dashj.demo.R
import org.dash.dashj.demo.ui.BaseListFragment


class AddressHierarchyFragment : BaseListFragment<AddressHierarchyAdapter, AddressHierarchyViewModel>() {

    override val progressMessageResId: Int
        get() = R.string.default_empty_state_message

    companion object {
        fun newInstance() = AddressHierarchyFragment()
    }

    override fun viewModelType(): Class<AddressHierarchyViewModel> = AddressHierarchyViewModel::class.java

    override fun createAdapter(): AddressHierarchyAdapter {
        return AddressHierarchyAdapter(activity!!)
    }

    override fun initView() {
        super.initView()
        activity!!.setTitle(R.string.fragment_address_hierarchy_title)
    }

    override fun bindViewModel(viewModel: AddressHierarchyViewModel) {
        viewModel.keyChain.observe(this, Observer { data ->
            data!!.run {
                adapter.replace(keys, wallet.networkParameters)
                updateView(keys.isNotEmpty())
                setInfo("Addresses count: ${keys.size}")
            }
        })
    }

    override fun onRefresh() {

    }
}
