package com.parinya.worklog.ui.manage_work

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.textfield.TextInputLayout
import com.parinya.worklog.R
import com.parinya.worklog.databinding.FragmentAddWorkBinding
import com.parinya.worklog.databinding.FragmentManageHomeBinding
import com.parinya.worklog.db.Work
import com.parinya.worklog.db.WorkDatabase
import com.parinya.worklog.ui.add_work.AddWorkViewModel
import com.parinya.worklog.ui.add_work.AddWorkViewModelFactory
import com.parinya.worklog.ui.edit_work.EditWorkFragmentArgs

enum class ManageHomeType {
    Add,
    Edit,
}

class ManageHomeFragment : Fragment(R.layout.fragment_manage_home) {

    private lateinit var binding: FragmentManageHomeBinding
    private lateinit var viewModel: ManageHomeViewModel
    private val args: ManageHomeFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_manage_home, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dao = WorkDatabase.getInstance(view.context).workDao()
        val factory = ManageWorkViewModelFactory(dao)
        viewModel = ViewModelProvider(this, factory)[ManageHomeViewModel::class.java]

        binding.viewModel = viewModel

        val pageType = args.type
        val work = args.work

        if (pageType == ManageHomeType.Add) {
            binding.ipTimeOut.hint = getString(R.string.time_out)
            binding.ipActivity.hint = getString(R.string.activity)
            binding.ipKnowledge.hint = getString(R.string.knowledge)

            binding.btnSubmit.apply {
                text = getString(R.string.submit)
                setOnClickListener {
                    if (isAllInputValidated()) {
                        viewModel.saveWork()
                        findNavController().navigateUp()
                    }
                }
            }


        } else if (pageType == ManageHomeType.Edit) {
            viewModel.setWork(work ?: Work())

            binding.btnSubmit.apply {
                text = getString(R.string.update)
                setOnClickListener {
                    if (isAllInputValidated()) {
                        viewModel.updateWork()
                        findNavController().navigateUp()
                    }
                }
            }


        }
    }

    private fun isAllInputValidated(): Boolean {
        val errorMessage = "Please fill out this field."
        var activity: Boolean = true
        var knowledge: Boolean = true
        var timeOut: Boolean = true
        val date = validateInput(binding.ipDate, errorMessage)
        val timeIn = validateInput(binding.ipTimeIn, errorMessage)

        if (args.type == ManageHomeType.Edit) {
            timeOut = validateInput(binding.ipTimeOut, errorMessage)
            activity = validateInput(binding.ipActivity, errorMessage)
            knowledge = validateInput(binding.ipKnowledge, errorMessage)
        }

        return date && timeIn && timeOut && activity && knowledge
    }

    private fun validateInput(input: TextInputLayout, errorMessage: String): Boolean {
        return input.run {
            isErrorEnabled = false

            val value = editText?.text.toString()
            if (value.isBlank()) {
                isErrorEnabled = true
                error = errorMessage
                false
            } else {
                true
            }
        }
    }

}