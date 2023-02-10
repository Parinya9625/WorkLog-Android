package com.parinya.worklog.ui.home

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
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
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.parinya.worklog.FilterSortedBy
import com.parinya.worklog.R
import com.parinya.worklog.SharedViewModel
import com.parinya.worklog.databinding.FilterSheetBinding
import com.parinya.worklog.databinding.FragmentHomeBinding
import com.parinya.worklog.databinding.WorkLogDialogBinding
import com.parinya.worklog.db.work.Work
import com.parinya.worklog.db.work.WorkDao
import com.parinya.worklog.db.work.WorkDatabase
import com.parinya.worklog.ui.manage_work.ManageHomeType
import com.parinya.worklog.util.*

class HomeFragment : Fragment(R.layout.fragment_home), CustomToolbarMenu {

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

    override fun getOptionsMenu(): Int {
        return R.menu.home_menu
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onOptionsMenuItemSelected(item: MenuItem) {
        when (item.toString()) {
            "Filter" -> filterBottomSheetDialog()
            "Search" -> {
                val action = HomeFragmentDirections.actionHomeFragmentToSearchFragment()
                findNavController().navigate(action)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun filterBottomSheetDialog() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val sheetBinding = FilterSheetBinding.inflate(layoutInflater, null, false)
        sheetBinding.lifecycleOwner = this
        sheetBinding.viewModel = sharedViewModel

        val textInputLayout = sheetBinding.ipfDateRange
        Util.convertInputToDateRangePicker(
            childFragmentManager,
            textInputLayout,
            onDateRangeSet = {from, to ->
                sharedViewModel.setDateRange(from, to)
            }
        )

        when (sharedViewModel.getSortedBy()) {
            FilterSortedBy.DateAsc -> sheetBinding.filterSortByGroup.check(R.id.cfDateAscending)
            FilterSortedBy.DateDes -> sheetBinding.filterSortByGroup.check(R.id.cfDateDescending)
            FilterSortedBy.Uncompleted -> sheetBinding.filterSortByGroup.check(R.id.cfUncompleted)
            else -> {}
        }

        sheetBinding.filterSortByGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            when (checkedIds.firstOrNull()) {
                null -> sharedViewModel.setSortedBy(FilterSortedBy.None)
                R.id.cfDateAscending -> sharedViewModel.setSortedBy(FilterSortedBy.DateAsc)
                R.id.cfDateDescending -> sharedViewModel.setSortedBy(FilterSortedBy.DateDes)
                R.id.cfUncompleted -> sharedViewModel.setSortedBy(FilterSortedBy.Uncompleted)
            }
        }

        sheetBinding.btnClear.setOnClickListener {
            sharedViewModel.clear()
            sheetBinding.filterSortByGroup.clearCheck()
        }

        sheetBinding.btnDone.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.setContentView(sheetBinding.root)
        bottomSheetDialog.show()
    }


}