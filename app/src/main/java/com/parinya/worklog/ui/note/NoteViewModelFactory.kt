package com.parinya.worklog.ui.note

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.parinya.worklog.db.note.NoteDao

class NoteViewModelFactory(
    private val dao: NoteDao,
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
            return NoteViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown vew model class")
    }

}