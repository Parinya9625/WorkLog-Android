package com.parinya.worklog.ui.manage_note

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.parinya.worklog.db.note.NoteDao

class ManageNoteViewModelFactory(
    private val dao: NoteDao
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ManageNoteViewModel::class.java)) {
            return ManageNoteViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown view model class")
    }

}