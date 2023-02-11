package com.parinya.worklog.ui.work

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.parinya.worklog.R
import com.parinya.worklog.databinding.FragmentWorkBinding
import com.parinya.worklog.databinding.WorkFilterSheetBinding
import com.parinya.worklog.databinding.WorkLogDialogBinding
import com.parinya.worklog.db.WorkLogDatabase
import com.parinya.worklog.db.work.Work
import com.parinya.worklog.db.work.WorkDao
import com.parinya.worklog.ui.manage_work.ManageHomeType
import com.parinya.worklog.util.*

class WorkFragment : Fragment(R.layout.fragment_work) {

    private lateinit var viewModel: WorkViewModel
    private lateinit var binding: FragmentWorkBinding
    private var works: List<Work> = listOf()
    private lateinit var itemTouchHelper: ItemTouchHelper
    private lateinit var dao: WorkDao

    // FILTER
    private var _sortedBy = HomeFilterSortedBy.None
    private var _dateRange = Pair<Long, Long>(0, 0)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWorkBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Util.setupToolbar(this, binding.homeToolbar, R.menu.home_menu)
        setupToolbarMenuOnClick()
        setupViewModel()
        initRecyclerView(view)
        setupFAB()
    }

    override fun onResume() {
        super.onResume()
        updateSelectedFilter()
        updateRecyclerView()
    }

    private fun setupViewModel() {
        dao = WorkLogDatabase.getInstance(requireContext()).workDao()
        val factory = WorkViewModelFactory(dao)
        viewModel = ViewModelProvider(this, factory)[WorkViewModel::class.java]

        binding.viewModel = viewModel
    }

    private fun setupFAB() {
        binding.fabAddWork.setOnClickListener {
            val action = WorkFragmentDirections.actionHomeFragmentToManageHomeFragment(type = ManageHomeType.Add)
            findNavController().navigate(action)
        }
    }

    private fun updateSelectedFilter() {
        viewModel.sortedBy.observe(this) {sortedBy ->
            _sortedBy = sortedBy
            updateRecyclerView()
        }
        viewModel.pairDateRange.observe(this) {dateRange ->
            _dateRange = dateRange
            updateRecyclerView()
        }
    }

    private fun updateRecyclerView() {
        dao.getWorks(_sortedBy, _dateRange.first, _dateRange.second).observe(this) {worksList ->
            binding.rvWorks.items(worksList)
            works = worksList
        }
    }

    private fun initRecyclerView(view: View) {
        binding.rvWorks.apply {

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
                                    onResume()
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
                                WorkFragmentDirections.actionHomeFragmentToManageHomeFragment(
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun setupToolbarMenuOnClick() {
        binding.homeToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_home_search -> {
                    val action = WorkFragmentDirections.actionHomeFragmentToSearchFragment()
                    findNavController().navigate(action)
                }
                R.id.menu_home_filter -> filterBottomSheetDialog()
            }

            true
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun filterBottomSheetDialog() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val sheetBinding = WorkFilterSheetBinding.inflate(layoutInflater, null, false)
        sheetBinding.lifecycleOwner = this
        sheetBinding.viewModel = viewModel

        val textInputLayout = sheetBinding.ipfDateRange
        Util.convertInputToDateRangePicker(
            childFragmentManager,
            textInputLayout,
            onDateRangeSet = {from, to ->
                viewModel.setDateRange(from, to)
            }
        )

        when (viewModel.getSortedBy()) {
            HomeFilterSortedBy.DateAsc -> sheetBinding.filterSortByGroup.check(R.id.cfDateAscending)
            HomeFilterSortedBy.DateDes -> sheetBinding.filterSortByGroup.check(R.id.cfDateDescending)
            HomeFilterSortedBy.Uncompleted -> sheetBinding.filterSortByGroup.check(R.id.cfUncompleted)
            else -> {}
        }

        sheetBinding.filterSortByGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            when (checkedIds.firstOrNull()) {
                null -> viewModel.setSortedBy(HomeFilterSortedBy.None)
                R.id.cfDateAscending -> viewModel.setSortedBy(HomeFilterSortedBy.DateAsc)
                R.id.cfDateDescending -> viewModel.setSortedBy(HomeFilterSortedBy.DateDes)
                R.id.cfUncompleted -> viewModel.setSortedBy(HomeFilterSortedBy.Uncompleted)
            }
        }

        sheetBinding.btnClear.setOnClickListener {
            viewModel.clearFilter()
            sheetBinding.filterSortByGroup.clearCheck()
        }

        sheetBinding.btnDone.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.setContentView(sheetBinding.root)
        bottomSheetDialog.show()
    }

}