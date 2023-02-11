package com.parinya.worklog.ui.note

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parinya.worklog.db.note.Note
import com.parinya.worklog.db.note.NoteDao
import kotlinx.coroutines.launch
import kotlin.random.Random

class NoteViewModel(
    private val dao: NoteDao,
): ViewModel() {

    fun addNote() {
        val note = Note(
            title = "Hello ".repeat(Random.nextInt(1, 10)),
            text = "World ".repeat(Random.nextInt(5, 20)),
        )

        viewModelScope.launch {
            dao.insertNote(note)
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            dao.deleteNote(note)
        }
    }

}