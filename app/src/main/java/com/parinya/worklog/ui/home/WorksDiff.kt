package com.parinya.worklog.ui.home

import androidx.recyclerview.widget.DiffUtil
import com.parinya.worklog.db.work.Work

class WorksDiff(private val old: List<Work>?, private val new: List<Work>?): DiffUtil.Callback() {
    override fun getOldListSize(): Int = old?.size ?: 0

    override fun getNewListSize(): Int = new?.size ?: 0

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return old?.getOrNull(oldItemPosition) === new?.getOrNull(newItemPosition)
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return old?.getOrNull(oldItemPosition) == new?.getOrNull(newItemPosition)
    }
}