package com.parinya.worklog.ui.search_work

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
import com.parinya.worklog.R
import com.parinya.worklog.databinding.FragmentSearchWorkBinding
import com.parinya.worklog.databinding.WorkLogDialogBinding
import com.parinya.worklog.db.WorkLogDatabase
import com.parinya.worklog.db.work.Work
import com.parinya.worklog.db.work.WorkDao
import com.parinya.worklog.ui.manage_work.ManageHomeType
import com.parinya.worklog.ui.work.WorkLogDialogFragment
import com.parinya.worklog.ui.work.WorkRecyclerViewAdapter
import com.parinya.worklog.util.SwipeButton
import com.parinya.worklog.util.SwipeHelper
import com.parinya.worklog.util.Util
import com.parinya.worklog.util.WorkTileSwipeButton

class SearchWorkFragment : Fragment(R.layout.fragment_search_work) {

    private lateinit var binding: FragmentSearchWorkBinding
    private lateinit var viewModel: SearchWorkViewModel
    private lateinit var dao: WorkDao
    private var works: List<Work> = listOf()
    private lateinit var itemTouchHelper: ItemTouchHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchWorkBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        viewModel.searchQuery.observe(viewLifecycleOwner) {query ->
            dao.searchWorks(query.trim()).observe(viewLifecycleOwner) {works ->
                viewModel.searchResult.value = works
                this.works = works

                binding.bannerNoSearchResult.isVisible = query.isNotBlank() && works.isEmpty()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Util.setupToolbar(this, binding.searchWorkToolbar)
        setupViewModel()
        initRecyclerView()
    }

    private fun setupViewModel() {
        dao = WorkLogDatabase.getInstance(requireContext()).workDao()
        val factory = SearchWorkViewModelFactory(dao)
        viewModel = ViewModelProvider(this, factory)[SearchWorkViewModel::class.java]

        binding.viewModel = viewModel
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
        itemTouchHelper = ItemTouchHelper(object: SwipeHelper(recyclerView) {
            override fun swipeLeftButtons(position: Int): List<SwipeButton> {
                return listOf(
                    WorkTileSwipeButton.DeleteWorkSwipeButton(
                        requireContext(),
                        onClick = {
                            val builder = MaterialAlertDialogBuilder(requireContext())

                            builder.apply {
                                setIcon(R.drawable.ic_delete_32)
                                setTitle(getString(R.string.confirm_delete_work_title))
                                setMessage(getString(R.string.confirm_delete_work_message))
                                setPositiveButton(getString(R.string.delete)) { dialog, which ->
                                    viewModel.deleteWork(works[position])
                                }
                                setNegativeButton(getString(R.string.cancel)) { dialog, which ->

                                }
                                show()
                            }
                        }
                    ),
                    WorkTileSwipeButton.EditWorkSwipeButton(
                        requireContext(),
                        onClick = {
                            val action =
                                SearchWorkFragmentDirections.actionSearchFragmentToManageHomeFragment(
                                    work = works[position],
                                    type = ManageHomeType.Edit
                                )
                            findNavController().navigate(action)
                        },
                    ),
                )
            }

            override fun swipeRightButtons(position: Int): List<SwipeButton> {
                return listOf()
            }
        })
        itemTouchHelper.attachToRecyclerView(recyclerView)

    }
}