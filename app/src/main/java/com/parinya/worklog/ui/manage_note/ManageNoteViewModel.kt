package com.parinya.worklog.ui.manage_note

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parinya.worklog.db.note.Note
import com.parinya.worklog.db.note.NoteDao
import kotlinx.coroutines.launch

class ManageNoteViewModel(
    private val dao: NoteDao
): ViewModel() {

    var _note: Note = Note()

    val _content = MutableLiveData<String>()
    val content: LiveData<String> = _content

    private fun getTitleAndText(): List<String> {
        val split = content.value.toString().lines()
        val title = split.first().ifBlank { "" }
        val text = if (split.size >= 2) {
            split.minus(split.first()).joinToString(separator = "\n")
        } else { "" }

        return listOf(title, text)
    }

    private fun getNote(): Note {
        val note = getTitleAndText()

        return Note(
            title = note.first(),
            text = note.last(),
        )
    }

    private fun getUpdateNote(): Note {
        val note = getTitleAndText()

        return Note(
            id = _note.id,
            title = note.first(),
            text = note.last(),
        )
    }

    fun saveNote() {
        viewModelScope.launch {
            dao.insertNote(getNote())
        }
    }

    fun updateNote() {
        viewModelScope.launch {
            dao.updateNote(getUpdateNote())
        }
    }

    fun setNote(note: Note) {
        _note = note
        _content.value = "${note.title}\n${note.text}"
    }
}