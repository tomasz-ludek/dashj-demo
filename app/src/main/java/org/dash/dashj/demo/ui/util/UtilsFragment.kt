package org.dash.dashj.demo.ui.util

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.content.res.Resources
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.text.InputType
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.utils_fragment.view.*
import org.bitcoinj.core.AddressFormatException
import org.bitcoinj.core.DumpedPrivateKey
import org.dash.dashj.demo.MainActivity
import org.dash.dashj.demo.MainPreferences
import org.dash.dashj.demo.R
import org.dash.dashj.demo.Utils
import org.greenrobot.eventbus.EventBus
import java.util.*


class UtilsFragment : Fragment() {

    companion object {
        fun newInstance() = UtilsFragment()
    }

    private lateinit var viewModel: UtilsViewModel

    private lateinit var layoutView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        walletManager = WalletManager.getInstance()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        layoutView = inflater.inflate(R.layout.utils_fragment, container, false)
        initView()
        return layoutView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(UtilsViewModel::class.java)
        viewModel.walletInfo.observe(this, Observer {
            layoutView.walletView.text = it!!.walletName
            layoutView.networkView.text = it.networkParameters.javaClass.simpleName
            layoutView.balanceView.text = it.balance.toFriendlyString()
            layoutView.addressView.text = it.currentReceiveAddress.toBase58()
        })
        viewModel.blockchainState.observe(this, Observer {
            Log.d("blockchainState", "${it!!.blocksLeft},\t${it.bestChainHeight}")

            val initialising = (it.blocksLeft == 0 && it.bestChainHeight == 0)
            val viewState = if (initialising) 0 else 1
            if (layoutView.bottomInfoView.displayedChild != viewState) {
                layoutView.bottomInfoView.displayedChild = viewState
            }
            if (initialising) {
                layoutView.bottomInfoMessageView.text = ""
            } else {
                layoutView.bottomInfoMessageView.text = when {
                    it.blocksLeft == 0 -> "Blockchain synced (${Utils.format(Date())})"
                    else -> "Best chain date: ${Utils.format(it.bestChainDate)} (${it.bestChainHeight})\nBlocks left: ${it.blocksLeft}"
                }
            }
        })
        viewModel.djService.observe(this, Observer { _ ->
            //Toast.makeText(activity, if (djServiceLiveData != null) "Connected" else "Disconnected", Toast.LENGTH_LONG).show()
        })
        activity!!.setTitle(R.string.fragment_utils_title)
        (activity as MainActivity).setSubTitle(MainPreferences.getInstance().latestConfigName)
    }

    private fun initView() {
        layoutView.addressView.setOnClickListener {
            val clipboard = activity!!.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("currentReceiveAddress", layoutView.addressView.text)
            clipboard.primaryClip = clip
            Toast.makeText(activity, "address has been copied to clipboard", Toast.LENGTH_LONG).show()
            Log.d("currentReceiveAddress", layoutView.addressView.text.toString())
        }
        layoutView.toStringBtnView.setOnClickListener {
            Log.d("wallet.toString(...)", "wallet.toString(...) ${viewModel.walletToString()}")
        }
        layoutView.importAddressBtnView.setOnClickListener {
            val builder = AlertDialog.Builder(activity!!)
            builder.setTitle("Import key")

            val inputLayout = TextInputLayout(activity)
            val input = TextInputEditText(activity)
            input.hint = "Key (Base58)"
            input.setText("93RLnmbkkRXinkkEknsoEaPgYDjFThT6b7KKzjaD6jYYWUYS2a1")
            inputLayout.addView(input)

            input.inputType = InputType.TYPE_CLASS_TEXT
            val padding = dpToPx(16)
            inputLayout.setPadding(padding, padding, padding, padding)
            builder.setView(inputLayout)

            builder.setPositiveButton("Ok") { dialog, _ -> dialog.dismiss() }
            builder.setPositiveButton("Import") { _, _ -> importKey(input.text.toString()) }
            builder.setNegativeButton("Cancel", null)

            builder.show()
        }
    }

    private fun importKey(key: String) {
        if (TextUtils.isEmpty(key)) {
            Snackbar.make(layoutView, "Key cannot be empty", Snackbar.LENGTH_LONG).show()
            return
        }
        if (!DashAddressValidator.isValid(key)) {
            Snackbar.make(layoutView, "Incorrect format of key", Snackbar.LENGTH_LONG).show()
            return
        }
/*
        val wallet = walletManager.wallet
        var message: String
        try {
            val rawKey = DumpedPrivateKey.fromBase58(wallet.networkParameters, key)
            val imported = wallet.importKey(rawKey.key)
            message = if (imported) "Key imported successfully" else "Key already imported"
        } catch (x: AddressFormatException) {
            message = "Unable to import key: ${x.message}"
        }
        Snackbar.make(layoutView, message, Snackbar.LENGTH_LONG).setAction("Action", null).show()
*/
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }
}
