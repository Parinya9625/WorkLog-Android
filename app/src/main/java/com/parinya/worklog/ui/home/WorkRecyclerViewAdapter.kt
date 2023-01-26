package com.parinya.worklog.ui.home

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.parinya.worklog.BR
import com.parinya.worklog.R
import com.parinya.worklog.db.Work
import com.parinya.worklog.util.getAdapter

class WorkRecyclerViewAdapter(
    private val onClick: (Work) -> Unit = {},
    private val onEditSwiped: (Work) -> Unit = {},
    private val onDeleteSwiped: (Work) -> Unit = {},
): RecyclerView.Adapter<ViewHolder>()  {

    lateinit var context: Context
    lateinit var _recyclerView: RecyclerView
    var adapter: WorkRecyclerViewAdapter = this
    var worksList = arrayListOf<Work>()

    companion object {
        const val VIEW_W = 0
        const val VIEW_R = 1
        const val VIEW_G = 2
        const val VIEW_B = 3
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        _recyclerView = recyclerView
//        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    // TODO : Update
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val binding: ViewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.work_tile, parent, false)
//        return ViewHolder(binding)

        context = parent.context

        return when(viewType) {
            VIEW_W -> {
                val binding: ViewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.work_tile, parent, false)
                ViewHolder(binding)
            }
            VIEW_R -> {
                val binding: ViewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.work_tile_r, parent, false)
                ViewHolder(binding)
            }
            VIEW_G -> {
                val binding: ViewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.work_tile_g, parent, false)
                ViewHolder(binding)
            }
            VIEW_B -> {
                val binding: ViewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.work_tile_b, parent, false)
                ViewHolder(binding)
            }
            else -> {
                val binding: ViewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.work_tile, parent, false)
                ViewHolder(binding)
            }
        }

    }

    override fun getItemCount(): Int {
        return worksList.size
    }

    // TODO: Update
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(worksList[position], onClick)
    }

    override fun getItemViewType(position: Int): Int {
        return worksList[position].viewType
    }

    fun getWork(index: Int): Work? {
        return worksList.getOrNull(index)
    }

    fun updateWorks(works: List<Work>) {
        DiffUtil.calculateDiff(WorksDiffCallBack(worksList, works)).dispatchUpdatesTo(this)

        worksList.clear()
        worksList.addAll(works)
    }

}

// TODO: Update !
class ViewHolder(val binding: ViewDataBinding): RecyclerView.ViewHolder(binding.root) {
    fun bind(work: Work, onClick: (Work) -> Unit) {
        binding.apply {
            setVariable(BR.work, work)
            root.setOnClickListener {
                onClick(work)
            }
        }
    }
}