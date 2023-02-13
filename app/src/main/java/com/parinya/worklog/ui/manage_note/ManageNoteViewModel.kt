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

    val _title = MutableLiveData<String>()
    val title: LiveData<String> = _title

    val _text = MutableLiveData<String>()
    val text: LiveData<String> = _text

    private fun getNote(): Note {
        return Note(
            title = title.value ?: "",
            text = text.value ?: "",
        )
    }

    fun saveNote() {
        viewModelScope.launch {
            dao.insertNote(getNote())
        }
    }
}