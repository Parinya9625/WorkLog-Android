package com.parinya.worklog.ui.work

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.parinya.worklog.BR
import com.parinya.worklog.R
import com.parinya.worklog.db.work.Work

class WorkRecyclerViewAdapter(
    private val onClick: (Work) -> Unit = {},
): RecyclerView.Adapter<ViewHolder>()  {

    lateinit var context: Context
    lateinit var _recyclerView: RecyclerView
    var adapter: WorkRecyclerViewAdapter = this
    var worksList = arrayListOf<Work>()

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        _recyclerView = recyclerView
    }

    // TODO : Update
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context

        val binding: ViewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.work_tile, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return worksList.size
    }

    // TODO: Update
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(worksList[position], onClick)
    }

    fun getWork(index: Int): Work? {
        return worksList.getOrNull(index)
    }

    fun updateWorks(works: List<Work>) {
        DiffUtil.calculateDiff(WorksDiff(worksList, works)).dispatchUpdatesTo(this)

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