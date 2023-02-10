package com.parinya.worklog.ui.note

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.parinya.worklog.R
import com.parinya.worklog.databinding.FragmentNoteBinding
import com.parinya.worklog.db.WorkLogDatabase
import com.parinya.worklog.db.note.NoteDao

class NoteFragment : Fragment(R.layout.fragment_note) {

    private lateinit var binding: FragmentNoteBinding
    private lateinit var dao: NoteDao
    private lateinit var viewModel: NoteViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNoteBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dao = WorkLogDatabase.getInstance(view.context).noteDao()
        val factory = NoteViewModelFactory(dao)
        viewModel = ViewModelProvider(this, factory)[NoteViewModel::class.java]

        binding.viewModel = viewModel

    }

}