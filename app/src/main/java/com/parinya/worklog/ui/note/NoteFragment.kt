package com.parinya.worklog.ui.note

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.parinya.worklog.R
import com.parinya.worklog.databinding.FragmentNoteBinding
import com.parinya.worklog.db.WorkLogDatabase
import com.parinya.worklog.db.note.Note
import com.parinya.worklog.db.note.NoteDao
import com.parinya.worklog.util.Util
import com.parinya.worklog.util.items

class NoteFragment : Fragment(R.layout.fragment_note) {

    private lateinit var binding: FragmentNoteBinding
    private lateinit var dao: NoteDao
    private lateinit var viewModel: NoteViewModel
    private var notes: List<Note> = listOf()

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

        Util.setupToolbar(this, binding.noteToolbar)
        setupViewModel()
        setupFAB()
        initRecyclerView()

    }

    override fun onResume() {
        super.onResume()
        updateRecyclerView()
    }

    private fun setupViewModel() {
        dao = WorkLogDatabase.getInstance(requireContext()).noteDao()
        val factory = NoteViewModelFactory(dao)
        viewModel = ViewModelProvider(this, factory)[NoteViewModel::class.java]

        binding.viewModel = viewModel
    }

    private fun setupFAB() {
        binding.fabAddNote.setOnClickListener {
            viewModel.addNote()
            onResume()
        }
    }

    private fun initRecyclerView() {
        binding.rvNotes.apply {

            layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
            adapter = NoteRecyclerViewAdapter(
                onClick = {
                    viewModel.deleteNote(it)
                    onResume()
                }
            )

        }
    }

    private fun updateRecyclerView() {
        dao.getNotes().observe(viewLifecycleOwner) { notesList ->
            Log.d("WorkLog >", "${notesList.size} | ${notesList}")
            binding.rvNotes.items(notesList)
            notes = notesList
        }
    }
}