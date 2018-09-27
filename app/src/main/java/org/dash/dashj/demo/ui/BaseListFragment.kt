package org.dash.dashj.demo.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.base_list_fragment.view.*
import org.dash.dashj.demo.MainActivity
import org.dash.dashj.demo.MainPreferences
import org.dash.dashj.demo.R
import org.dash.dashj.demo.Utils
import org.dash.dashj.demo.event.WalletReloadEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

abstract class BaseListFragment<T : RecyclerView.Adapter<out RecyclerView.ViewHolder>, V : ViewModel> : Fragment() {

    companion object {
        private const val EMPTY_STATE_VIEW = 0
        private const val READY_STATE_VIEW = 1
    }

    private lateinit var layoutView: View

    protected lateinit var viewModel: V

    private lateinit var baseViewModel: BaseListViewModel

    protected lateinit var adapter: T

    protected abstract val emptyStateMessageResId: Int

    protected abstract fun viewModelType(): Class<V>

    protected abstract fun createAdapter(): T

    protected abstract fun onRefresh()

    protected abstract fun bindViewModel(viewModel: V)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        layoutView = inflater.inflate(R.layout.base_list_fragment, container, false)
        initView()
        return layoutView
    }

    protected open fun initView() {
        (activity as MainActivity).setSubTitle(MainPreferences.getInstance().latestConfigName)
        layoutView.infoView.visibility = View.GONE
        layoutView.recyclerView.layoutManager = LinearLayoutManager(context)
        layoutView.recyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        this.adapter = createAdapter()
        layoutView.recyclerView.adapter = this.adapter

        layoutView.refreshView.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark)
        layoutView.refreshView.canChildScrollUp()
        layoutView.refreshView.setOnRefreshListener {
            onRefresh()
        }

        layoutView.emptyStateMessage.setText(emptyStateMessageResId)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        baseViewModel = ViewModelProviders.of(this).get(BaseListViewModel::class.java)
        bindBaseViewModel()
        viewModel = ViewModelProviders.of(this).get(viewModelType())
        bindViewModel(viewModel)
    }

    protected open fun bindBaseViewModel() {
        baseViewModel.blockchainState.observe(this, Observer {
            val message = when {
                (it!!.blocksLeft < 0 || (it.blocksLeft == 0 && it.bestChainHeight == 0)) -> "Loading info..."
                it.blocksLeft == 0 -> "Blockchain synced (${Utils.format(Date())})"
                else -> "Best chain date: ${Utils.format(it.bestChainDate)} (${it.bestChainHeight})\nBlocks left: ${it.blocksLeft}"
            }
            layoutView.bottomInfoView.visibility = View.VISIBLE
            layoutView.bottomInfoView.text = message
        })
    }

    protected fun updateView(showReadyState: Boolean) {
        layoutView.refreshView.isRefreshing = false
        layoutView.rootAnimatorView.displayedChild =
                if (showReadyState) READY_STATE_VIEW else EMPTY_STATE_VIEW
    }

    protected fun setInfo(text: CharSequence) {
        layoutView.infoView.visibility = View.VISIBLE
        layoutView.infoView.text = text
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    fun onWalletReloadEvent(event: WalletReloadEvent) {

    }
}
