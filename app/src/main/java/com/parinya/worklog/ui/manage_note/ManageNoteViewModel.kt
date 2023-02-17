package com.parinya.worklog.ui.manage_note

import android.graphics.Color
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

    val _title = MutableLiveData<String>()
    val title: LiveData<String> = _title

    val _text = MutableLiveData<String>()
    val text: LiveData<String> = _text

    val _color = MutableLiveData<Int>()
    val color: LiveData<Int> = _color

    private fun getNote(): Note {
        return Note(
            title = _title.value ?: "",
            text = _text.value ?: "",
            color = _color.value ?: Color.TRANSPARENT,
        )
    }

    private fun getUpdateNote(): Note {
        return Note(
            id = _note.id,
            title = _title.value ?: "",
            text = _text.value ?: "",
            color = _color.value ?: Color.TRANSPARENT,
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
        _title.value = note.title
        _text.value = note.text
        _color.value = note.color
    }
}