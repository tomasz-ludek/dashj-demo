package org.dash.dashj.demo.ui.evouserlist

import android.app.ProgressDialog
import android.arch.lifecycle.Observer
import android.content.res.Resources
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.v7.app.AlertDialog
import android.text.InputType
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import org.bitcoinj.core.Coin
import org.bitcoinj.evolution.EvolutionUser
import org.dash.dashj.demo.R
import org.dash.dashj.demo.ui.BaseListFragment


class EvoUserListFragment : BaseListFragment<EvoUserListAdapter, EvoUserListViewModel>() {

    private lateinit var progressDialog: ProgressDialog

    override val progressMessageResId: Int
        get() = R.string.default_empty_state_message

    override val emptyStateMessageResId: Int
        get() = R.string.evo_user_list_empty_state_message

    override val emptyStateIconResId: Int
        get() = R.drawable.hmm

    companion object {
        fun newInstance() = EvoUserListFragment()
    }

    override fun viewModelType(): Class<EvoUserListViewModel> = EvoUserListViewModel::class.java

    override fun createAdapter(): EvoUserListAdapter {
        return EvoUserListAdapter(activity, object : EvoUserListAdapter.OnOptionClickListener {
            override fun onTopUp(evoUser: EvolutionUser?) {
                showNumericInputDialog("Top-up ${evoUser!!.userName}", "Credits", "Top-up", "1.00",
                        object : OnDialogActionListener<String>() {
                            override fun onDialogAction(param: String) {
                                val credits = Coin.parseCoin(param)
                                viewModel.topUpUser(evoUser, credits)
                            }
                        })
            }

            override fun onReset(evoUser: EvolutionUser?) {
                showDialog("Reset ${evoUser!!.userName}", "Are you sure you want to reset ${evoUser.userName}", "Reset",
                        object : OnDialogActionListener<Any>() {
                            override fun onDialogAction() {
                                viewModel.reset(evoUser)
                            }
                        })
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
            updateView(if (it.isEmpty()) EMPTY_STATE_VIEW else READY_STATE_VIEW)
            setInfo("Users count: ${it.size}")
        })
        viewModel.djService.observe(this, Observer { _ ->
            //Toast.makeText(activity, if (djServiceLiveData != null) "Connected" else "Disconnected", Toast.LENGTH_LONG).show()
        })
        viewModel.showMessageAction.observe(this, Observer {
            val title = it!!.first
            val message = it.second
            progressDialog.dismiss()
            showDialog(title, message)
        })
        viewModel.showErrorMessageAction.observe(this, Observer {
            val title = it!!.first
            val message = it.second
            progressDialog.dismiss()
            showDialog(title, message)
        })
        viewModel.showProgressDialogAction.observe(this, Observer {
            progressDialog = ProgressDialog.show(activity, null, it, true)
            progressDialog.setCancelable(true)
            progressDialog.show()
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.evo_user_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.action_create_user -> showTextInputDialog("Create user (SubTxRegister)", "Username", "Create", "derp",
                    object : OnDialogActionListener<String>() {
                        override fun onDialogAction(param: String) {
                            viewModel.createUser(param, Coin.parseCoin("0.001"))
                        }
                    })
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showDialog(title: String, message: String, actionLabel: String? = "Ok", listener: OnDialogActionListener<Any>? = null) {
        AlertDialog.Builder(activity!!).run {
            setTitle(title)
            setMessage(message)
            if (listener != null) {
                setNegativeButton("Cancel", null)
                setPositiveButton(actionLabel) { _, _ ->
                    listener.onDialogAction()
                }
            } else {
                setPositiveButton(actionLabel, null)
            }
            show();
        }
    }

    private fun showTextInputDialog(title: String, hint: String, actionLabel: String, value: String, listener: OnDialogActionListener<String>) {
        val textInput = createTextInput(InputType.TYPE_CLASS_TEXT, hint, value)
        showInputDialog(textInput, title, actionLabel, listener)
    }

    private fun showNumericInputDialog(title: String, hint: String, actionLabel: String, value: String, listener: OnDialogActionListener<String>) {
        val numericInputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        val numericInput = createTextInput(numericInputType, hint, value)
        showInputDialog(numericInput, title, actionLabel, listener)
    }

    private fun showInputDialog(inputLayout: TextInputLayout, title: String, actionLabel: String, listener: OnDialogActionListener<String>) {
        val builder = AlertDialog.Builder(activity!!)
        builder.setTitle(title)
        builder.setView(inputLayout)
        builder.setPositiveButton(actionLabel) { _, _ ->
            listener.onDialogAction(inputLayout.editText!!.text.toString())
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    private fun createTextInput(inputType: Int, hint: String, value: String): TextInputLayout {
        val inputLayout = TextInputLayout(activity)
        val input = TextInputEditText(activity)
        input.hint = hint
        inputLayout.addView(input)
        input.inputType = inputType
        input.append(value)

        val padding = dpToPx(16)
        inputLayout.setPadding(padding, padding, padding, padding)

        return inputLayout
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }

    open class OnDialogActionListener<T> {

        open fun onDialogAction(param: T) {}

        open fun onDialogAction() {}
    }
}
