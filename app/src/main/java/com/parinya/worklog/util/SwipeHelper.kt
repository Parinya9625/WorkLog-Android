/*
    SwipeHelper V.1.1 by Parinya Wongmanee

    [Class]
    SwipeHelper  -  object for ItemTouchhelper
    SwipeButton  -  button in SwipeHelper
    SwipeTitle   -  text inside button
    SwipeIcon    -  icon inside button

    [Example Code]
    val itemTouchHelper = ItemTouchHelper(
       object: SwipeHelper(recyclerView) {
            override fun swipeLeftButtons(position: Int): List<SwipeButton> {
                return listOf(
                    SwipeButton(
                        context = context,
                        backgroundColor = Color.RED,
                        swipeTitle = SwipeTitle(
                            context,
                            "Hello World !"
                        ),
                        swipeIcon = SwipeIcon(
                            applicationContext,
                            ContextCompat.getDrawable(context, R.drawable.baseline_face_6_24)!!,
                        ),
                        onClick = {
                            Toast.makeText(context, "RED $position", Toast.LENGTH_SHORT).show()
                        }
                    ),
                    SwipeButton(...),
                )
            }
            override fun swipeRightButtons(position: Int): List<SwipeButton> {
                return listOf(...)
            }
       }
    )
 */

package com.parinya.worklog.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.drawable.updateBounds
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.math.max
import kotlin.math.min

@SuppressLint("ClickableViewAccessibility")
abstract class SwipeHelper(
    private val recyclerView: RecyclerView,
): ItemTouchHelper.SimpleCallback(
    ItemTouchHelper.ACTION_STATE_IDLE,
    ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
) {
    private var currentSwipeDirection: Int = -1
    private var currentSwipePosition = -1
    private var recoverSwipeBuffer = LinkedList<Int>()
    private var allSwipeLeftButtons: MutableMap<Int, List<SwipeButton>> = mutableMapOf()
    private var allSwipeRightButtons: MutableMap<Int, List<SwipeButton>> = mutableMapOf()

    abstract fun swipeLeftButtons(position: Int): List<SwipeButton>
    abstract fun swipeRightButtons(position: Int): List<SwipeButton>

    init {
        recyclerView.setOnTouchListener { view, motionEvent ->
            if (currentSwipePosition < 0) return@setOnTouchListener false

            recoverSwipeBuffer.add(currentSwipePosition)

            if (currentSwipeDirection == ItemTouchHelper.LEFT) {
                allSwipeLeftButtons[currentSwipePosition]?.forEach { swipeButton ->
                    swipeButton.handleClick(motionEvent)
                }
            } else if (currentSwipeDirection == ItemTouchHelper.RIGHT) {
                allSwipeRightButtons[currentSwipePosition]?.forEach { swipeButton ->
                    swipeButton.handleClick(motionEvent)
                }
            }

            currentSwipePosition = -1
            recoverSwipedItem()
            true
        }
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        currentSwipePosition = position
        recoverSwipedItem()
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
        // active list tile position
        val position = viewHolder.adapterPosition
        var maxDX = dX

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            if (dX < 0) {
                // BUTTON ON RIGHT SIDE
                currentSwipeDirection = ItemTouchHelper.LEFT
                // when button don't set to list tile
                if (!allSwipeLeftButtons.containsKey(position)) {
                    allSwipeLeftButtons[position] = swipeLeftButtons(position)
                }
                val buttons = allSwipeLeftButtons[position] ?: return
                maxDX = max(-buttons.width(), dX)
                drawActiveButtons(c, buttons, viewHolder.itemView, maxDX)

            } else if (dX > 0) {
                // BUTTON ON LEFT SIDE
                currentSwipeDirection = ItemTouchHelper.RIGHT

                if (!allSwipeRightButtons.containsKey(position)) {
                    allSwipeRightButtons[position] = swipeRightButtons(position)
                }
                val buttons = allSwipeRightButtons[position] ?: return
                maxDX = min(buttons.width(), dX)
                drawActiveButtons(c, buttons, viewHolder.itemView, maxDX)
            }
        }

        super.onChildDraw(c, recyclerView, viewHolder, maxDX, dY, actionState, isCurrentlyActive)
    }

    private fun recoverSwipedItem() {
        while (recoverSwipeBuffer.isNotEmpty()) {
            val position = recoverSwipeBuffer.poll() ?: return
            recyclerView.adapter?.notifyItemChanged(position)
        }
    }

    private fun drawActiveButtons(canvas: Canvas, buttons: List<SwipeButton>, itemView: View, dX: Float) {
        if (dX < 0) {
            var right: Float = itemView.right.toFloat()

            buttons.forEach { swipeButton ->
                swipeButton.draw(
                    canvas,
                    RectF(
                        right - swipeButton.width, // L
                        itemView.top.toFloat(), // T
                        right, // R
                        itemView.bottom.toFloat(), // B
                    ),
                    isLeft = buttons.lastOrNull() == swipeButton,
                    isRight = buttons.firstOrNull() == swipeButton,
                )

                right -= swipeButton.width
            }
        } else if (dX > 0) {
            var left: Float = itemView.left.toFloat()

            buttons.forEach { swipeButton ->
                swipeButton.draw(
                    canvas,
                    RectF(
                        left, // L
                        itemView.top.toFloat(), // T
                        left + swipeButton.width, // R
                        itemView.bottom.toFloat(), // B
                    ),
                    isLeft = buttons.firstOrNull() == swipeButton,
                    isRight = buttons.lastOrNull() == swipeButton,
                )

                left += swipeButton.width
            }
        }
    }
}

class SwipeButton(
    private val context: Context,
    private val backgroundColor: Int = Color.BLACK,
    private val swipeIcon: SwipeIcon? = null,
    private val swipeTitle: SwipeTitle? = null,
    private val horizontalPadding: Float = 48f,
    private val onClick: () -> Unit = {},
) {
    private var clickableArea: RectF? = null

    val width: Float

    init {
        val maxIconWidth = (horizontalPadding * 2) + (swipeIcon?.sizeInDensity() ?: 0f)
        val maxTitleWidth = swipeTitle?.bounds?.width()?.plus(horizontalPadding * 2) ?: 0f

        width = max(maxIconWidth, maxTitleWidth)
    }

    fun draw(canvas: Canvas, rectF: RectF, isLeft: Boolean = false, isRight: Boolean = false) {
        clickableArea = rectF

        // --- VALUE ---
        var paint = Paint()
        val spacing = 4f * context.resources.displayMetrics.density
        val cornerRadius = 18f

        var totalContentHeight = 0f
        totalContentHeight += swipeIcon?.height()?.toFloat() ?: 0f
        totalContentHeight += swipeTitle?.height()?.toFloat() ?: 0f
        totalContentHeight += if (swipeIcon != null && swipeTitle != null) spacing else 0f

        val rectCenter = Rect(
            (rectF.centerX() - (width / 2)).toInt(),
            (rectF.centerY() - (totalContentHeight / 2)).toInt(),
            (rectF.centerX() + (width / 2)).toInt(),
            (rectF.centerY() + (totalContentHeight / 2)).toInt(),
        )

        // --- DRAW BACKGROUND ---
        paint = Paint()
        paint.color = backgroundColor

        val aloneButton = floatArrayOf(cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius)
        val leftButton = floatArrayOf(cornerRadius, cornerRadius, 0f, 0f, 0f, 0f, cornerRadius, cornerRadius)
        val rightButton = floatArrayOf(0f, 0f, cornerRadius, cornerRadius, cornerRadius, cornerRadius, 0f, 0f)
        val middleButton = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
        val corner = when {
            isLeft && isRight -> aloneButton
            isLeft -> leftButton
            isRight -> rightButton
            else -> middleButton
        }

        val path = Path()
        path.addRoundRect(rectF, corner, Path.Direction.CW)
        canvas.drawPath(path, paint)

        // --- DRAW ICON ---
        if (swipeIcon != null) {
            val icon = swipeIcon.getDrawable(rectF.left, rectF.top)
            val iconBaseBounds = icon.bounds
            icon.updateBounds(
                left = rectCenter.centerX() - (iconBaseBounds.width() / 2),
                top = rectCenter.top,
                right = rectCenter.centerX() + (iconBaseBounds.width() / 2),
                bottom = rectCenter.top + swipeIcon.height(),
            )
            icon.draw(canvas)
        }

        // --- DRAW TITLE ---
        if (swipeTitle != null) {
            canvas.drawText(
                swipeTitle.title,
                (rectCenter.centerX() - (swipeTitle.bounds.width() / 2)).toFloat(),
                (rectCenter.bottom).toFloat(),
                swipeTitle.paint,
            )
        }
    }

    fun handleClick(event: MotionEvent) {
        clickableArea?.let {area ->
            if (area.contains(event.x, event.y) && event.action == MotionEvent.ACTION_DOWN) {
                onClick()
            }
        }
    }
}

data class SwipeIcon(
    val context: Context,
    private val drawable: Drawable,
    val size: Float = 32f,
    val color: Int? = Color.WHITE,
) {
    fun height(): Int {
        return drawable.bounds.height()
    }

    fun getDrawable(x: Float, y: Float): Drawable {
        drawable.setBounds(
            (x).toInt(),
            (y).toInt(),
            (x + sizeInDensity()).toInt(),
            (y + sizeInDensity()).toInt(),
        )
        if (color != null) {
            drawable.setTint(color)
        }
        return drawable
    }

    fun sizeInDensity(): Float {
        return size * context.resources.displayMetrics.density
    }
}

data class SwipeTitle(
    val context: Context,
    val title: String,
    val fontSize: Float = 12f,
    val color: Int = Color.WHITE,
) {
    val paint = Paint()
    val bounds = Rect()

    init {
        paint.color = color
        paint.textSize = fontSize * context.resources.displayMetrics.density
        paint.typeface = Typeface.DEFAULT_BOLD
        paint.getTextBounds(title, 0, title.length, bounds)
    }

    fun height(): Int {
        return bounds.height()
    }
}

private fun List<SwipeButton>.width(): Float {
    if (isEmpty()) return 0.0f
    return map { it.width }.reduce { acc, fl -> acc + fl }
}