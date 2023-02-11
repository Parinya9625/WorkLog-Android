package com.parinya.worklog.ui.note

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.parinya.worklog.BR
import com.parinya.worklog.databinding.NoteTileBinding
import com.parinya.worklog.db.note.Note

class NoteRecyclerViewAdapter(
    private val onClick: (Note) -> Unit = {},
): RecyclerView.Adapter<ViewHolder>() {

    lateinit var _recyclerView: RecyclerView
    var notesList = arrayListOf<Note>()

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        _recyclerView = recyclerView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = NoteTileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = notesList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(notesList[position], onClick)
    }

    fun updateNotes(notes: List<Note>) {
        DiffUtil.calculateDiff(NotesDiff(notesList, notes)).dispatchUpdatesTo(this)

        notesList.clear()
        notesList.addAll(notes)
    }

}

class ViewHolder(val binding: ViewDataBinding): RecyclerView.ViewHolder(binding.root) {
    fun bind(note: Note, onClick: (Note) -> Unit) {
        binding.apply {
            setVariable(BR.note, note)
            root.setOnClickListener { onClick(note) }
        }
    }
}