package com.parinya.worklog.ui.note

import androidx.recyclerview.widget.DiffUtil
import com.parinya.worklog.db.note.Note

class NotesDiff(private val old: List<Note>?, private val new: List<Note>?): DiffUtil.Callback() {

    override fun getOldListSize(): Int = old?.size ?: 0

    override fun getNewListSize(): Int = new?.size ?: 0

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return old?.getOrNull(oldItemPosition) === new?.getOrNull(newItemPosition)
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return old?.getOrNull(oldItemPosition) == new?.getOrNull(newItemPosition)
    }

}