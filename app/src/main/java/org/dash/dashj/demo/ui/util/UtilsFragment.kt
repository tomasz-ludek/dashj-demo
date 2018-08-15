package org.dash.dashj.demo.ui.util

import android.arch.lifecycle.ViewModelProviders
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
import kotlinx.android.synthetic.main.utils_fragment.view.*
import org.bitcoinj.core.AddressFormatException
import org.bitcoinj.core.DumpedPrivateKey
import org.dash.dashj.demo.MainActivity
import org.dash.dashj.demo.R
import org.dash.dashj.demo.WalletManager


class UtilsFragment : Fragment() {

    companion object {
        fun newInstance() = UtilsFragment()
    }

    private lateinit var viewModel: UtilsViewModel

    private lateinit var layoutView: View

    private lateinit var walletManager: WalletManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        walletManager = WalletManager.getInstance()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        layoutView = inflater.inflate(R.layout.utils_fragment, container, false)
        initView()
        return layoutView
    }

    private fun initView() {
        layoutView.toStringBtnView.setOnClickListener {
            val toString = walletManager.wallet.toString(true, true, true, null)
            Log.d("wallet.toString(...)", "wallet.toString(...) $toString")
        }
        layoutView.importAddressBtnView.setOnClickListener {
            //            val keyChainGroup = walletManager.wallet.activeKeyChain
//            val keys = RestrictedAccessUtil.invokeGetKeys(keyChainGroup, false)
//            (RestrictedAccessUtil.invokeGetKeys(keyChainGroup, false)[0] as DeterministicKey).pathAsString
//            val chainAsString = keyChainGroup.toString(false, walletManager.wallet.networkParameters)
//            Log.d("chainAsString", chainAsString)
/*
            val wallet = walletManager.wallet
            try {
                val rawKey = DumpedPrivateKey.fromBase58(wallet.networkParameters, "93RLnmbkkRXinkkEknsoEaPgYDjFThT6b7KKzjaD6jYYWUYS2a1")
                val imported = wallet.importKey(rawKey.key)

                val toString = wallet.toString(true, true, true, null)
                Log.d("wallet.toString(...)", "wallet.toString(...) {imported + $imported} $toString")
            } catch (x: AddressFormatException) {
                Toast.makeText(activity, x.message, Toast.LENGTH_LONG).show()
            }
*/
            val builder = AlertDialog.Builder(activity!!)
            builder.setTitle("Import address")

            val inputLayout = TextInputLayout(activity)
            val input = TextInputEditText(activity)
            input.hint = "Address"
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
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(UtilsViewModel::class.java)

        activity!!.setTitle(R.string.fragment_utils_title)
        (activity as MainActivity).setSubTitle(WalletManager.getInstance().configName)
    }

}
