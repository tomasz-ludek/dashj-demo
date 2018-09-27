package org.dash.dashj.demo.ui.evouserlist

import android.arch.lifecycle.Observer
import android.os.Build
import android.support.v7.app.AlertDialog
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import org.bitcoinj.core.Coin
import org.bitcoinj.evolution.EvolutionUser
import org.dash.dashj.demo.R
import org.dash.dashj.demo.ui.BaseListFragment


class EvoUserListFragment : BaseListFragment<EvoUserListAdapter, EvoUserListViewModel>() {

    override val emptyStateMessageResId: Int
        get() = R.string.default_empty_state_message

    companion object {
        fun newInstance() = EvoUserListFragment()
    }

    override fun viewModelType(): Class<EvoUserListViewModel> = EvoUserListViewModel::class.java

    override fun createAdapter(): EvoUserListAdapter {
        return EvoUserListAdapter(activity, object : EvoUserListAdapter.OnOptionClickListener {
            override fun onTopUp(evoUser: EvolutionUser?) {
                viewModel.topUpUser(evoUser!!, Coin.COIN)
            }

            override fun onReset(evoUser: EvolutionUser?) {
                viewModel.reset(evoUser!!)
            }
        })
    }

    override fun initView() {
        super.initView()
        activity!!.setTitle(R.string.fragment_evo_user_list_title)
        setHasOptionsMenu(true)
    }

    override fun bindViewModel(viewModel: EvoUserListViewModel) {
        viewModel.evoUsers.observe(this, Observer {
            adapter.replace(it!!)
            updateView(it.isNotEmpty())
            setInfo("Users count: ${it.size}")
        })
        viewModel.djService.observe(this, Observer { djServiceLiveData ->
            Toast.makeText(activity, if (djServiceLiveData != null) "Connected" else "Disconnected", Toast.LENGTH_LONG).show()
        })
        viewModel.showMessageAction.observe(this, Observer {
            val title = it!!.first
            val message = it.second
            showDialog(title, message, false)
        })
        viewModel.showErrorMessageAction.observe(this, Observer {
            val title = it!!.first
            val message = it.second
            showDialog(title, message, true)
        })
    }

    override fun onRefresh() {

    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.evo_user_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.action_create_user -> viewModel.createUser()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showDialog(title: String, message: String, alert: Boolean = false) {
        val builder: AlertDialog.Builder
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            builder = AlertDialog.Builder(activity!!, android.R.style.Theme_Material_Dialog_Alert);
//        } else {
//            builder = AlertDialog.Builder(activity!!);
//        }
        builder = AlertDialog.Builder(activity!!);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok) { dialog, _ ->
                    dialog.cancel()
                }
//                .setNegativeButton(android.R.string.cancel) { _, _ ->
//
//                }
                .setIcon(if (alert) android.R.drawable.ic_dialog_alert else android.R.drawable.ic_dialog_info)
                .show();
    }
}
