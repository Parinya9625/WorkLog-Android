package com.parinya.worklog.ui.home

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import android.widget.Toolbar
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.parinya.worklog.R
import com.parinya.worklog.databinding.FragmentHomeBinding
import com.parinya.worklog.databinding.WorkLogDialogBinding
import com.parinya.worklog.db.Work
import com.parinya.worklog.db.WorkDao
import com.parinya.worklog.db.WorkDatabase
import com.parinya.worklog.ui.add_work.AddWorkFragment
import com.parinya.worklog.ui.manage_work.ManageHomeType
import com.parinya.worklog.util.SwipeHelper
import com.parinya.worklog.util.items
import kotlinx.coroutines.coroutineScope

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: FragmentHomeBinding
//    private lateinit var recyclerView: RecyclerView
    private lateinit var dao: WorkDao

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dao = WorkDatabase.getInstance(view.context).workDao()
        val factory = HomeViewModelFactory(dao)
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]

        binding.viewModel = viewModel

        initRecyclerView(view)

        binding.fabAdd.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToManageHomeFragment(type = ManageHomeType.Add)
            findNavController().navigate(action)
        }
    }

    override fun onResume() {
        super.onResume()

            dao.getWorks().observe(this) {
                binding.rvWorks.items(it)
            }
    }

    private fun initRecyclerView(view: View) {
//        recyclerView = view.findViewById<RecyclerView>(R.id.rvWorks)
        binding.rvWorks.apply {
//            layoutManager = LinearLayoutManager(context)

            val gridColumnCount = resources.getInteger(R.integer.grid_column_count)
            layoutManager = GridLayoutManager(context, gridColumnCount)

            adapter = WorkRecyclerViewAdapter(
                onClick = {
                    val dialogBinding: WorkLogDialogBinding = DataBindingUtil.inflate(
                        layoutInflater, R.layout.work_log_dialog, null, false
                    )
                    dialogBinding.work = it
                    WorkLogDialogFragment(dialogBinding.root).show(childFragmentManager, "custom dialog")
                },
                onEditSwiped = {
                    val action = HomeFragmentDirections.actionHomeFragmentToManageHomeFragment(
                        work = it,
                        type = ManageHomeType.Edit
                    )
                    findNavController().navigate(action)
                },
                onDeleteSwiped = {
                    confirmDeleteWorkDialog(context, it)
                }
            )
        }

        ItemTouchHelper(object : SwipeHelper(binding.rvWorks) {
            override fun instantiateUnderlayButton(position: Int): List<UnderlayButton> {
                val buttons = listOf(
                    deleteWorkButton(viewModel.works.value?.get(position) ?: Work()),
                    editWorkButton(viewModel.works.value?.get(position) ?: Work()),
                )

                return buttons
            }
        }).attachToRecyclerView(binding.rvWorks)

    }

    private fun editWorkButton(work: Work): SwipeHelper.UnderlayButton {
        return SwipeHelper.UnderlayButton(
            this.requireContext(),
            "Edit",
            14.0f,
            Color.rgb(245, 191, 66),
            object : SwipeHelper.UnderlayButtonClickListener {
                override fun onClick() {
                    val action = HomeFragmentDirections.actionHomeFragmentToManageHomeFragment(
                        work = work,
                        type = ManageHomeType.Edit
                    )
                    findNavController().navigate(action)
                }

            },
        )
    }

    private fun deleteWorkButton(work: Work): SwipeHelper.UnderlayButton {
        return SwipeHelper.UnderlayButton(
            this.requireContext(),
            "Delete",
            14.0f,
            Color.RED,
            object : SwipeHelper.UnderlayButtonClickListener {
                override fun onClick() {
                    confirmDeleteWorkDialog(requireContext(), work)
                }

            },
//            icon = ContextCompat.getDrawable(this.requireContext(), R.drawable.ic_delete_32)
        )
    }

    private fun confirmDeleteWorkDialog(context: Context, work: Work) {
        val builder = MaterialAlertDialogBuilder(context)

        builder.apply {
            setIcon(R.drawable.baseline_delete_forever_24)
            setTitle("Confirm delete work")
            setMessage("Are you sure you want to delete this work?")
            setPositiveButton("Delete") { dialog, which ->
                viewModel.deleteWork(work)
            }
            setNegativeButton("Cancel") { dialog, which ->

            }
            show()
        }
    }
}