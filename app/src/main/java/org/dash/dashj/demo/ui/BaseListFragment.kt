package org.dash.dashj.demo.ui

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
import org.dash.dashj.demo.R

abstract class BaseListFragment<T : RecyclerView.Adapter<out RecyclerView.ViewHolder>, V : ViewModel> : Fragment() {

    companion object {
        private const val EMPTY_STATE_VIEW = 0
        private const val READY_STATE_VIEW = 1
    }

    private lateinit var layoutView: View

    private lateinit var viewModel: V

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
        viewModel = ViewModelProviders.of(this).get(viewModelType())
        bindViewModel(viewModel)
    }

    protected fun updateView(showReadyState: Boolean) {
        layoutView.rootAnimatorView.displayedChild =
                if (showReadyState) READY_STATE_VIEW else EMPTY_STATE_VIEW
    }
}
