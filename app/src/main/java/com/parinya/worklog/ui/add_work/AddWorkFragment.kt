package com.parinya.worklog.ui.add_work

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import com.parinya.worklog.R
import com.parinya.worklog.databinding.FragmentAddWorkBinding
import com.parinya.worklog.db.WorkDatabase

class AddWorkFragment : Fragment(R.layout.fragment_add_work) {

    private lateinit var binding: FragmentAddWorkBinding
    private lateinit var viewModel: AddWorkViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_work, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dao = WorkDatabase.getInstance(view.context).workDao()
        val factory = AddWorkViewModelFactory(dao)
        viewModel = ViewModelProvider(this, factory)[AddWorkViewModel::class.java]

        binding.viewModel = viewModel

        binding.btnSubmit.setOnClickListener {
            if (isAllInputValidated()) {
                viewModel.saveWorkToDB()
                findNavController().navigateUp()
            }
        }
    }

    private fun isAllInputValidated(): Boolean {
        val errorMessage = "Test error message"

        val date = validateInput(binding.ipDate, errorMessage)
        val timeIn = validateInput(binding.ipTimeIn, errorMessage)
        val activity = validateInput(binding.ipActivity, errorMessage)
        val knowledge = validateInput(binding.ipKnowledge, errorMessage)

        return date && timeIn && activity && knowledge
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