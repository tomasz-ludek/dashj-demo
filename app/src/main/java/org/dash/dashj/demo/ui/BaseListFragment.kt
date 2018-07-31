package org.dash.dashj.demo.ui

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

abstract class BaseListFragment : Fragment() {

    companion object {
        private const val EMPTY_STATE_VIEW = 0
        private const val READY_STATE_VIEW = 1
    }

    private lateinit var layoutView: View

    protected abstract val adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>

    protected abstract val emptyStateMessageResId: Int

    protected abstract fun onRefresh()

    protected abstract fun bindViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        layoutView = inflater.inflate(R.layout.base_list_fragment, container, false)
        initView()
        return layoutView
    }

    protected open fun initView() {
        layoutView.recyclerView.layoutManager = LinearLayoutManager(context)
        layoutView.recyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        layoutView.recyclerView.adapter = adapter

        layoutView.refreshView.setOnRefreshListener {
            onRefresh()
        }

        layoutView.emptyStateMessage.setText(emptyStateMessageResId)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bindViewModel()
    }

    protected fun updateView(showEmptyState: Boolean) {
        layoutView.rootAnimatorView.displayedChild =
                if (showEmptyState) EMPTY_STATE_VIEW else READY_STATE_VIEW
    }
}
