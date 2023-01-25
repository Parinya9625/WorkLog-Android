package com.parinya.worklog.ui.home

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.parinya.worklog.BR
import com.parinya.worklog.R
import com.parinya.worklog.db.Work
import com.parinya.worklog.util.getAdapter

class WorkRecyclerViewAdapter(
    private val onClick: (Work) -> Unit = {},
    private val onEditSwiped: (Work) -> Unit = {},
    private val onDeleteSwiped: (Work) -> Unit = {},
): RecyclerView.Adapter<ViewHolder>()  {

    lateinit var context: Context
    lateinit var _recyclerView: RecyclerView
    var adapter: WorkRecyclerViewAdapter = this
    var worksList = arrayListOf<Work>()
    val itemTouchHelper = ItemTouchHelper(object: ItemTouchHelper.SimpleCallback(
        0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            Log.i("WorkLog >", "Direction ${direction}")

            if (direction == ItemTouchHelper.LEFT) {
                // DELETE

//                val isDeleted = onDeleteSwiped(worksList[viewHolder.adapterPosition])
//
//                if (isDeleted) {
//                    val index = viewHolder.adapterPosition
//                    val newWorkList = worksList.clone() as ArrayList<Work>
//                    newWorkList.removeAt(index)
//                    updateWorks(newWorkList)
//                } else {
//                    adapter.notifyItemChanged(viewHolder.adapterPosition)
//                }

                onDeleteSwiped(worksList[viewHolder.adapterPosition])
                adapter.notifyItemChanged(viewHolder.adapterPosition)
            } else if (direction == ItemTouchHelper.RIGHT) {
                // EDIT
                onEditSwiped(worksList[viewHolder.adapterPosition])
                adapter.notifyItemChanged(viewHolder.adapterPosition)
            }
        }

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            // dx > 0 : Swipe left to right
            // dx < 0 : Swipe right to left

            val isCancelled = dX == 0.0f && !isCurrentlyActive

            if (isCancelled) {
                clearCanvas(c, viewHolder)
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                return
            }

            if (dX < 0) {
                val deleteDrawable = ContextCompat.getDrawable(context, R.drawable.ic_delete_32)
                drawSwipeBackground(c, viewHolder.itemView, Color.RED, deleteDrawable, isIconOnLeft = false)
            } else if (dX > 0) {
                val editDrawable = ContextCompat.getDrawable(context, R.drawable.ic_edit_32)
                drawSwipeBackground(c, viewHolder.itemView, Color.rgb(245, 191, 66), editDrawable)
            }

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }

        private fun drawSwipeBackground(c: Canvas, itemView: View, color: Int, icon: Drawable?, isIconOnLeft: Boolean = true) {
            val paint = Paint()
            paint.color = color

            c.drawRoundRect(
                RectF(
                    itemView.left.toFloat(),
                    itemView.top.toFloat(),
                    itemView.right.toFloat(),
                    itemView.bottom.toFloat(),
                ),
                16f, 16f,
                paint,
            )

            if (icon != null) {
                val intrinsicWidth = icon.intrinsicWidth
                val intrinsicHeight = icon.intrinsicHeight
                val iconMargin = (itemView.height - intrinsicHeight) / 2

                val boundLeft = if (isIconOnLeft) {
                    itemView.left + iconMargin
                } else {
                    itemView.right - iconMargin - intrinsicWidth
                }
                val boundRight = if (isIconOnLeft) {
                    itemView.left + iconMargin + intrinsicHeight
                } else {
                    itemView.right - iconMargin
                }
                val boundTop = itemView.top + (itemView.height - intrinsicHeight) / 2
                val boundBottom = boundTop + intrinsicHeight

                icon.setBounds(boundLeft, boundTop, boundRight, boundBottom)
                icon.draw(c)
            }

        }

        private fun clearCanvas(c: Canvas, viewHolder: RecyclerView.ViewHolder) {
            val itemView = viewHolder.itemView
            c.drawRect(
                RectF(0f, 0f, 0f, 0f),
                Paint()
            )
        }
    })

    companion object {
        const val VIEW_W = 0
        const val VIEW_R = 1
        const val VIEW_G = 2
        const val VIEW_B = 3
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        _recyclerView = recyclerView
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val binding: ViewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.work_tile, parent, false)
//        return ViewHolder(binding)

        context = parent.context

        return when(viewType) {
            VIEW_W -> {
                val binding: ViewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.work_tile, parent, false)
                ViewHolder(binding)
            }
            VIEW_R -> {
                val binding: ViewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.work_tile_r, parent, false)
                ViewHolder(binding)
            }
            VIEW_G -> {
                val binding: ViewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.work_tile_g, parent, false)
                ViewHolder(binding)
            }
            VIEW_B -> {
                val binding: ViewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.work_tile_b, parent, false)
                ViewHolder(binding)
            }
            else -> {
                val binding: ViewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.work_tile, parent, false)
                ViewHolder(binding)
            }
        }

    }

    override fun getItemCount(): Int {
        return worksList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(worksList[position], onClick)
    }

    override fun getItemViewType(position: Int): Int {
        return worksList[position].viewType
    }

    fun updateWorks(works: List<Work>) {
        DiffUtil.calculateDiff(WorksDiffCallBack(worksList, works)).dispatchUpdatesTo(this)

        worksList.clear()
        worksList.addAll(works)
    }

}

class ViewHolder(val binding: ViewDataBinding): RecyclerView.ViewHolder(binding.root) {
    fun bind(work: Work, onClick: (Work) -> Unit) {
        binding.apply {
            setVariable(BR.work, work)
            root.setOnClickListener {
                onClick(work)
            }
        }
    }
}