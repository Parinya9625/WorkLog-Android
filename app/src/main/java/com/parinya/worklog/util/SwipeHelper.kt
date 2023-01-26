package com.parinya.worklog.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.math.abs
import kotlin.math.max

abstract class SwipeHelper(
    private val recyclerView: RecyclerView
) : ItemTouchHelper.SimpleCallback(
    ItemTouchHelper.ACTION_STATE_IDLE,
    ItemTouchHelper.LEFT
) {
    private var swipedPosition = -1
    private val buttonsBuffer: MutableMap<Int, List<UnderlayButton>> = mutableMapOf()
    private val recoverQueue = object : LinkedList<Int>() {
        override fun add(element: Int): Boolean {
            if (contains(element)) return false
            return super.add(element)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private val touchListener = View.OnTouchListener { _, event ->
        if (swipedPosition < 0) return@OnTouchListener false
        buttonsBuffer[swipedPosition]?.forEach { it.handle(event) }
        recoverQueue.add(swipedPosition)
        swipedPosition = -1
        recoverSwipedItem()
        true
    }

    init {
        recyclerView.setOnTouchListener(touchListener)
    }

    private fun recoverSwipedItem() {
        while (!recoverQueue.isEmpty()) {
            val position = recoverQueue.poll() ?: return
            recyclerView.adapter?.notifyItemChanged(position)
        }
    }

    private fun drawButtons(
        canvas: Canvas,
        buttons: List<UnderlayButton>,
        itemView: View,
        dX: Float
    ) {
        var right = itemView.right
        buttons.forEach { button ->
            val width = button.intrinsicWidth / buttons.intrinsicWidth() * abs(dX)
            val left = right - width
            button.draw(
                canvas,
                RectF(left, itemView.top.toFloat(), right.toFloat(), itemView.bottom.toFloat()),
                isFirst = buttons.last() === button,
                isLast = buttons.first() === button,
            )

            right = left.toInt()
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
        val position = viewHolder.adapterPosition
        var maxDX = dX
        val itemView = viewHolder.itemView

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            if (dX < 0) {
                if (!buttonsBuffer.containsKey(position)) {
                    buttonsBuffer[position] = instantiateUnderlayButton(position)
                }

                val buttons = buttonsBuffer[position] ?: return
                if (buttons.isEmpty()) return
                maxDX = max(-buttons.intrinsicWidth(), dX)
                drawButtons(c, buttons, itemView, maxDX)
            }
        }

        super.onChildDraw(
            c,
            recyclerView,
            viewHolder,
            maxDX,
            dY,
            actionState,
            isCurrentlyActive
        )
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        if (swipedPosition != position) recoverQueue.add(swipedPosition)
        swipedPosition = position
        recoverSwipedItem()
    }

    abstract fun instantiateUnderlayButton(position: Int): List<UnderlayButton>

    //region UnderlayButton
    interface UnderlayButtonClickListener {
        fun onClick()
    }

    class UnderlayButton(
        private val context: Context,
        private val title: String,
        textSize: Float,
//        @ColorRes private val colorRes: Int,
        private val color: Int,
        private val clickListener: UnderlayButtonClickListener,
        private val icon: Drawable? = null,
    ) {
        private var clickableRegion: RectF? = null
        private val textSizeInPixel: Float = textSize * context.resources.displayMetrics.density // dp to px
        private val horizontalPadding = 50.0f
        val intrinsicWidth: Float

        init {
            val paint = Paint()
            paint.textSize = textSizeInPixel
            paint.typeface = Typeface.DEFAULT_BOLD
            paint.textAlign = Paint.Align.LEFT
            val titleBounds = Rect()
            paint.getTextBounds(title, 0, title.length, titleBounds)
            intrinsicWidth = titleBounds.width() + 2 * horizontalPadding
        }

        fun draw(canvas: Canvas, rect: RectF, isFirst: Boolean = false, isLast: Boolean = false) {
            val paint = Paint()

            // Draw background
            paint.color = color
            val r = 18f
            val firstBtnRadius = floatArrayOf(
                r, r,   // Top left radius in px
                0f, 0f,   // Top right radius in px
                0f, 0f,     // Bottom right radius in px
                r, r      // Bottom left radius in px
            )
            val lastBtnRadius = floatArrayOf(0f, 0f, r, r, r, r, 0f, 0f,)
            val onlyBtnRadius = floatArrayOf(r, r, r, r, r, r, r, r)
            val corners = if (isFirst && isLast) {
                onlyBtnRadius
            } else if (isFirst && !isLast) {
                firstBtnRadius
            } else if (!isFirst && isLast){
                lastBtnRadius
            } else {
                floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
            }
            val path = Path()
            path.addRoundRect(rect, corners, Path.Direction.CW)
            canvas.drawPath(path, paint)

            // Draw title
            paint.color = ContextCompat.getColor(context, android.R.color.white)
            paint.textSize = textSizeInPixel
            paint.typeface = Typeface.DEFAULT_BOLD
            paint.textAlign = Paint.Align.LEFT

            val titleBounds = Rect()
            paint.getTextBounds(title, 0, title.length, titleBounds)

            val y = rect.height() / 2 + titleBounds.height() / 2 - titleBounds.bottom
            canvas.drawText(title, rect.left + horizontalPadding, rect.top + y, paint)

//            // TODO: Draw icon
//            if (icon != null) {
//                icon.setBounds(
//                    (rect.left + horizontalPadding - 16).toInt(),
//                    (rect.top + 16).toInt(),
//                    (rect.right - horizontalPadding + 16).toInt(),
//                    (rect.top + y).toInt(),
//                )
//                icon.draw(canvas)
//            }

            clickableRegion = rect
        }

        fun handle(event: MotionEvent) {
            clickableRegion?.let {
                if (it.contains(event.x, event.y)) {
                    clickListener.onClick()
                }
            }
        }
    }
    //endregion
}

private fun List<SwipeHelper.UnderlayButton>.intrinsicWidth(): Float {
    if (isEmpty()) return 0.0f
    return map { it.intrinsicWidth }.reduce { acc, fl -> acc + fl }
}