package com.parinya.worklog.ui.manage_work

import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.textfield.TextInputLayout
import com.parinya.worklog.R
import com.parinya.worklog.databinding.FragmentManageWorkBinding
import com.parinya.worklog.db.WorkLogDatabase
import com.parinya.worklog.db.work.Work
import com.parinya.worklog.util.Util

enum class ManageHomeType {
    Add,
    Edit,
}

class ManageWorkFragment : Fragment(R.layout.fragment_manage_work) {

    private lateinit var binding: FragmentManageWorkBinding
    private lateinit var viewModel: ManageWorkViewModel
    private val args: ManageWorkFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentManageWorkBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Util.setupToolbar(this, binding.manageWorkToolbar)
        setupViewModel()
        setupPageType()
        setupPicker()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupViewModel() {
        val dao = WorkLogDatabase.getInstance(requireContext()).workDao()
        val factory = ManageWorkViewModelFactory(dao)
        viewModel = ViewModelProvider(this, factory)[ManageWorkViewModel::class.java]

        binding.viewModel = viewModel
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupPageType() {
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupPicker() {
        Util.convertInputToDatePicker(
            requireActivity().supportFragmentManager,
            binding.ipDate,
            onDateSet = {calendar ->
                viewModel.setDate(
                    inString = Util.dateToString(calendar.timeInMillis),
                    inMS = calendar.timeInMillis,
                )
            }
        )

        Util.convertInputToTimePicker(
            requireActivity().supportFragmentManager,
            binding.ipTimeIn,
            onTimeSet = {hour, minute ->
                viewModel.setTimeIn(String.format("%02d:%02d", hour, minute))
            }
        )

        Util.convertInputToTimePicker(
            requireActivity().supportFragmentManager,
            binding.ipTimeOut,
            onTimeSet = {hour, minute ->
                viewModel.setTimeOut(String.format("%02d:%02d", hour, minute))
            }
        )
    }

    private fun isAllInputValidated(): Boolean {
        val errorMessage = getString(R.string.error_fill_out_filed)
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