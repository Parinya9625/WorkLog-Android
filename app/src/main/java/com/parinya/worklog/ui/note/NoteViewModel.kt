package com.parinya.worklog.ui.note

import androidx.lifecycle.ViewModel
import com.parinya.worklog.db.note.NoteDao

class NoteViewModel(
    private val dao: NoteDao,
): ViewModel() {

}