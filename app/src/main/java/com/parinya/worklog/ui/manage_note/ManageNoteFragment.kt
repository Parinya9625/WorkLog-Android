package com.parinya.worklog.ui.manage_note

import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.AbsoluteSizeSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.parinya.worklog.R
import com.parinya.worklog.databinding.FragmentManageNoteBinding
import com.parinya.worklog.db.WorkLogDatabase
import com.parinya.worklog.db.note.Note
import com.parinya.worklog.db.note.NoteDao
import com.parinya.worklog.util.Util

enum class ManageNoteType {
    Add,
    Edit,
}

class ManageNoteFragment : Fragment(R.layout.fragment_manage_note) {

    private lateinit var binding: FragmentManageNoteBinding
    private lateinit var viewModel: ManageNoteViewModel
    private lateinit var dao: NoteDao
    private val args: ManageNoteFragmentArgs by navArgs()

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
        setupViewModel()
//        setupToolbarMenuOnClick()
        setupPageType()
        initEditText() // must run before addTextChangedListener
        setupRichEditText()
    }

    private fun setupViewModel() {
        dao = WorkLogDatabase.getInstance(requireContext()).noteDao()
        val factory = ManageNoteViewModelFactory(dao)
        viewModel = ViewModelProvider(this, factory)[ManageNoteViewModel::class.java]

        binding.viewModel = viewModel
    }

    private fun setupAddToolbarMenuOnClick() {
        binding.manageNoteToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_manage_note_save -> {
                    if (!viewModel.content.value.isNullOrBlank()) {
                        viewModel.saveNote()
                        findNavController().navigateUp()
                    } else {
                        Toast.makeText(context, "Nothing to save", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            true
        }
    }

    private fun setupEditToolbarMenuOnClick() {
        binding.manageNoteToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_manage_note_save -> {
                    if (!viewModel.content.value.isNullOrBlank()) {
                        viewModel.updateNote()
                        findNavController().navigateUp()
                    } else {
                        Toast.makeText(context, "Note can't be empty", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            true
        }
    }

    private fun setupPageType() {
        val pageType = args.type
        val note = args.note

        if (pageType == ManageNoteType.Add) {
            setupAddToolbarMenuOnClick()
        } else if (pageType == ManageNoteType.Edit) {
            viewModel.setNote(note ?: Note())
            setupEditToolbarMenuOnClick()
        }
    }

    private fun setupRichEditText() {
        val textLine = viewModel.content.value.toString().lines()

        binding.ipNoteText.editText!!.addTextChangedListener(object : TextWatcher {
            var canUpdateTitle = textLine.size <= 1
            var canUpdateText = textLine.size > 1

            override fun beforeTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) { }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) { }

            override fun afterTextChanged(editable: Editable) {
                val split = editable.toString().lines()

                if (split.size <= 1 && canUpdateTitle) {
                    canUpdateTitle = false
                    canUpdateText = true

                    binding.ipNoteText.editText?.let {
                        setSpanTitle(it, editable.toString())
                    }
                } else if (split.size > 1 && canUpdateText) {
                    canUpdateTitle = true
                    canUpdateText = false

                    binding.ipNoteText.editText?.let {
                        setSpanTitle(it, editable.toString())
                    }
                }
            }
        })
    }

    private fun setSpanTitle(editText: EditText, text: String) {
        val split = text.lines()

        val span = SpannableStringBuilder(text).apply {
            setSpan(StyleSpan(Typeface.BOLD), 0, split.first().length, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
            setSpan(AbsoluteSizeSpan((32 * requireContext().resources.displayMetrics.density).toInt()), 0, split.first().length, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
        }

        editText.text = span
        editText.setSelection(text.length)
    }

    private fun initEditText() {
        binding.ipNoteText.editText?.let {
            setSpanTitle(it, viewModel.content.value.toString())
        }
    }

}