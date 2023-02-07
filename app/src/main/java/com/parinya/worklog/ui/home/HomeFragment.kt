package com.parinya.worklog.ui.home

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.parinya.worklog.FilterSortedBy
import com.parinya.worklog.R
import com.parinya.worklog.SharedViewModel
import com.parinya.worklog.databinding.FragmentHomeBinding
import com.parinya.worklog.databinding.WorkLogDialogBinding
import com.parinya.worklog.db.Work
import com.parinya.worklog.db.WorkDao
import com.parinya.worklog.db.WorkDatabase
import com.parinya.worklog.ui.manage_work.ManageHomeType
import com.parinya.worklog.util.SwipeButton
import com.parinya.worklog.util.SwipeHelper
import com.parinya.worklog.util.WorkTileSwipeButton
import com.parinya.worklog.util.items

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var viewModel: HomeViewModel
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var binding: FragmentHomeBinding
    private var works: List<Work> = listOf()
    private lateinit var itemTouchHelper: ItemTouchHelper
//    private lateinit var recyclerView: RecyclerView
    private lateinit var dao: WorkDao

    // FILTER
    private var _sortedBy = FilterSortedBy.None
    private var _dateRange = Pair<Long, Long>(0, 0)

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

        sharedViewModel.sortedBy.observe(this) {sortedBy ->
            _sortedBy = sortedBy
            updateRecyclerView()
        }
        sharedViewModel.pairDateRange.observe(this) {dateRange ->
            _dateRange = dateRange
            updateRecyclerView()
        }

        updateRecyclerView()
    }

    private fun updateRecyclerView() {
        dao.getWorks(_sortedBy, _dateRange.first, _dateRange.second).observe(this) {worksList ->
            binding.rvWorks.items(worksList)
            works = worksList
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
                }
            )
        }
        attachItemTouchHelper(binding.rvWorks)
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
                                HomeFragmentDirections.actionHomeFragmentToManageHomeFragment(
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