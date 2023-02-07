package com.parinya.worklog.util

import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat
import com.parinya.worklog.R

class WorkTileSwipeButton {

    companion object {

        fun EditWorkSwipeButton(context: Context, onClick: () -> Unit): SwipeButton {
            return SwipeButton(
                context = context,
                backgroundColor = Color.rgb(245, 191, 66),
                swipeIcon = SwipeIcon(
                    context = context,
                    drawable = ContextCompat.getDrawable(context, R.drawable.ic_edit_32)!!,
                ),
                swipeTitle = SwipeTitle(
                    context = context,
                    title = "Edit",
                ),
                onClick = onClick,
            )
        }

        fun DeleteWorkSwipeButton(context: Context, onClick: () -> Unit): SwipeButton {
            return SwipeButton(
                context = context,
                backgroundColor = Color.RED,
                swipeIcon = SwipeIcon(
                    context = context,
                    drawable = ContextCompat.getDrawable(context, R.drawable.ic_delete_32)!!,
                ),
                swipeTitle = SwipeTitle(
                    context = context,
                    title = "Delete",
                ),
                onClick = onClick,
            )
        }

    }
}