package org.dash.dashj.demo.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.base_list_fragment.view.*
import org.dash.dashj.demo.MainActivity
import org.dash.dashj.demo.MainPreferences
import org.dash.dashj.demo.R
import org.dash.dashj.demo.Utils
import java.util.*

abstract class BaseListFragment<T : RecyclerView.Adapter<out RecyclerView.ViewHolder>, V : ViewModel> : Fragment() {

    companion object {
        const val EMPTY_STATE_VIEW = 0
        const val PROGRESS_STATE_VIEW = 1
        const val READY_STATE_VIEW = 2
    }

    private lateinit var layoutView: View

    protected lateinit var viewModel: V

    private lateinit var baseViewModel: BaseListViewModel

    protected lateinit var adapter: T

    protected open val emptyStateMessageResId: Int = -1

    protected open val emptyStateIconResId: Int = R.drawable.ic_dash_d_gray_24dp

    protected open val progressMessageResId: Int = -1

    protected abstract fun viewModelType(): Class<V>

    protected abstract fun createAdapter(): T

    protected open fun onRefresh() {}

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
        layoutView.refreshView.isEnabled = false
        layoutView.rootAnimatorView.displayedChild = EMPTY_STATE_VIEW

        layoutView.emptyStateIcon.setImageResource(emptyStateIconResId)

        if (emptyStateMessageResId > 0) {
            layoutView.emptyStateMessage.setText(emptyStateMessageResId)
            layoutView.emptyStateMessage.visibility = View.VISIBLE
        }

        if (progressMessageResId > 0) {
            layoutView.progressMessage.setText(progressMessageResId)
        }
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
    }

    protected fun updateView(showReadyState: Boolean) {
        updateView(if (showReadyState) READY_STATE_VIEW else PROGRESS_STATE_VIEW)
    }

    protected fun updateView(state: Int) {
        layoutView.refreshView.isRefreshing = false
        if (state != layoutView.rootAnimatorView.displayedChild) {
            layoutView.rootAnimatorView.displayedChild = state
        }
    }

    protected fun setInfo(text: CharSequence) {
        layoutView.infoView.visibility = View.VISIBLE
        layoutView.infoView.text = text
    }
}
