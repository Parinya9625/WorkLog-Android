package com.parinya.worklog.ui.edit_work

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.parinya.worklog.R
import com.parinya.worklog.databinding.FragmentEditWorkBinding
import com.parinya.worklog.db.WorkDatabase
import com.parinya.worklog.ui.add_work.AddWorkViewModel
import com.parinya.worklog.ui.add_work.AddWorkViewModelFactory

class EditWorkFragment : Fragment(R.layout.fragment_edit_work) {

    private lateinit var binding: FragmentEditWorkBinding
    private lateinit var viewModel: EditWorkViewModel
    private val args: EditWorkFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_work, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dao = WorkDatabase.getInstance(view.context).workDao()
        val factory = EditWorkViewModelFactory(dao)
        viewModel = ViewModelProvider(this, factory)[EditWorkViewModel::class.java]

        binding.viewModel = viewModel

        val work = args.work
        viewModel.setWork(work)

        binding.btnUpdate.setOnClickListener {
            confirmUpdateDialog(
                view.context,
                onUpdate = {
                    viewModel.updateWork()
                    findNavController().navigateUp()
                },
                onCancel = {},
            )
        }

        binding.btnDelete.setOnClickListener {
            confirmDeleteDialog(
                view.context,
                onDelete = {
                    viewModel.deleteWork()
                    findNavController().navigateUp()
                },
                onCancel = {
//                    findNavController().navigateUp()
                },
            )
        }
    }

    fun confirmUpdateDialog(context: Context, onUpdate: () -> Unit, onCancel: () -> Unit) {
        val builder = MaterialAlertDialogBuilder(context)

        builder.apply {
            setIcon(R.drawable.baseline_update_24)
            setTitle("Confirm update work")
            setMessage("Are you sure you want to update this work?")
            setPositiveButton("Update") { dialog, which ->
                onUpdate()
            }
            setNegativeButton("Cancel") { dialog, which ->
                onCancel()
            }
            show()
        }
    }

    fun confirmDeleteDialog(context: Context, onDelete: () -> Unit, onCancel: () -> Unit) {
        val builder = MaterialAlertDialogBuilder(context)

        builder.apply {
            setIcon(R.drawable.baseline_delete_forever_24)
            setTitle("Confirm delete work")
            setMessage("Are you sure you want to delete this work?")
            setPositiveButton("Delete") { dialog, which ->
                onDelete()
            }
            setNegativeButton("Cancel") { dialog, which ->
                onCancel()
            }
            show()
        }
    }
}