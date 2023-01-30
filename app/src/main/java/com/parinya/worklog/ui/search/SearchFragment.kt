package com.parinya.worklog.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.parinya.worklog.MainActivity
import com.parinya.worklog.R
import com.parinya.worklog.databinding.FragmentSearchBinding
import com.parinya.worklog.databinding.WorkLogDialogBinding
import com.parinya.worklog.db.Work
import com.parinya.worklog.db.WorkDao
import com.parinya.worklog.db.WorkDatabase
import com.parinya.worklog.ui.home.WorkLogDialogFragment
import com.parinya.worklog.ui.home.WorkRecyclerViewAdapter
import com.parinya.worklog.ui.manage_work.ManageHomeType
import com.parinya.worklog.util.SwipeHelper
import com.parinya.worklog.util.WorkTileSwipeButton

class SearchFragment : Fragment(R.layout.fragment_search) {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var viewModel: SearchViewModel
    private lateinit var dao: WorkDao
    private var works: List<Work> = listOf()
    private lateinit var itemTouchHelper: ItemTouchHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        setVisibleOptionsMenu(false)

        viewModel.searchQuery.observe(viewLifecycleOwner) {query ->
            dao.searchWorks(query.trim()).observe(viewLifecycleOwner) {works ->
                viewModel.searchResult.value = works
                this.works = works

                binding.bannerNoSearchResult.isVisible = query.isNotBlank() && works.isEmpty()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        setVisibleOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dao = WorkDatabase.getInstance(view.context).workDao()
        val factory = SearchViewModelFactory(dao)
        viewModel = ViewModelProvider(this, factory)[SearchViewModel::class.java]

        binding.viewModel = viewModel

        initRecyclerView()
    }

    private fun initRecyclerView() {
        binding.rvSearchResult.apply {
            layoutManager = LinearLayoutManager(context)

            adapter = WorkRecyclerViewAdapter(
                onClick = {
                    val dialogBinding: WorkLogDialogBinding = DataBindingUtil.inflate(
                        layoutInflater, R.layout.work_log_dialog, null, false
                    )
                    dialogBinding.work = it
                    WorkLogDialogFragment(dialogBinding.root).show(childFragmentManager, "WorkLogDialog")
                }
            )

        }
        attachItemTouchHelper(binding.rvSearchResult)
    }

    private fun attachItemTouchHelper(recyclerView: RecyclerView) {
        itemTouchHelper = ItemTouchHelper(object : SwipeHelper(recyclerView) {
            override fun instantiateUnderlayButton(position: Int): List<UnderlayButton> {
                val buttons = listOf(
                    WorkTileSwipeButton.DeleteWorkButton(requireContext()) {
                        val builder = MaterialAlertDialogBuilder(requireContext())

                        builder.apply {
                            setIcon(R.drawable.ic_delete_32)
                            setTitle("Confirm delete work")
                            setMessage("Are you sure you want to delete this work?")
                            setPositiveButton("Delete") { dialog, which ->
                                viewModel.deleteWork(works[position])
                            }
                            setNegativeButton("Cancel") { dialog, which ->

                            }
                            show()
                        }
                    },

                    WorkTileSwipeButton.EditWorkButton(requireContext()) {
                        val action =
                            SearchFragmentDirections.actionSearchFragmentToManageHomeFragment(
                                work = works[position],
                                type = ManageHomeType.Edit
                            )
                        findNavController().navigate(action)
                    }
                )

                return buttons
            }
        })
        itemTouchHelper.attachToRecyclerView(recyclerView)

    }

    private fun setVisibleOptionsMenu(value: Boolean) {
        (activity as MainActivity).setVisibleOptionsMenu(value)
    }

}