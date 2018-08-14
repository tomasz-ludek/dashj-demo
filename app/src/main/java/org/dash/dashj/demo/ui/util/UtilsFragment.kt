package org.dash.dashj.demo.ui.util

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.utils_fragment.view.*
import org.dash.dashj.demo.MainActivity
import org.dash.dashj.demo.R
import org.dash.dashj.demo.WalletManager


class UtilsFragment : Fragment() {

    companion object {
        fun newInstance() = UtilsFragment()
    }

    private lateinit var viewModel: UtilsViewModel

    private lateinit var layoutView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        layoutView = inflater.inflate(R.layout.utils_fragment, container, false)
        initView()
        return layoutView
    }

    private fun initView() {
        layoutView.toStringBtnView.setOnClickListener {
            val toString = WalletManager.getInstance().wallet.toString(true, true, true, null)
            Log.d("wallet.toString(...)", "wallet.toString(...) $toString")
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(UtilsViewModel::class.java)

        activity!!.setTitle(R.string.fragment_utils_title)
        (activity as MainActivity).setSubTitle(WalletManager.getInstance().configName)
    }

}
