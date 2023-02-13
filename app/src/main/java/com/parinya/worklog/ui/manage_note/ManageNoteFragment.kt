package com.parinya.worklog.ui.manage_note

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.parinya.worklog.R
import com.parinya.worklog.databinding.FragmentManageNoteBinding
import com.parinya.worklog.db.WorkLogDatabase
import com.parinya.worklog.db.note.NoteDao
import com.parinya.worklog.util.Util

class ManageNoteFragment : Fragment(R.layout.fragment_manage_note) {

    private lateinit var binding: FragmentManageNoteBinding
    private lateinit var viewModel: ManageNoteViewModel
    private lateinit var dao: NoteDao

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentManageNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Util.setupToolbar(this, binding.manageNoteToolbar, R.menu.manage_note_menu)
        binding.manageNoteToolbar.title = null

        setupViewModel()
        setupToolbarMenuOnClick()
        Util.setupClearFocusWhenDone(binding.ipNoteTitle)
    }

    private fun setupViewModel() {
        dao = WorkLogDatabase.getInstance(requireContext()).noteDao()
        val factory = ManageNoteViewModelFactory(dao)
        viewModel = ViewModelProvider(this, factory)[ManageNoteViewModel::class.java]

        binding.viewModel = viewModel
    }

    private fun setupToolbarMenuOnClick() {
        binding.manageNoteToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_manage_note_save -> {
                    viewModel.saveNote()
                    findNavController().navigateUp()
                }
            }

            true
        }
    }

}