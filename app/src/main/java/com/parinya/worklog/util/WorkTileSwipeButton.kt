package com.parinya.worklog.util

import android.content.Context
import android.graphics.Color

class WorkTileSwipeButton {

    companion object {

        fun EditWorkButton(context: Context, onClick: () -> Unit): SwipeHelper.UnderlayButton {
            return SwipeHelper.UnderlayButton(
                context,
                "Edit",
                14.0f,
                Color.rgb(245, 191, 66),
                object : SwipeHelper.UnderlayButtonClickListener {
                    override fun onClick() {
                        onClick()
                    }

                },
            )
        }

        fun DeleteWorkButton(context: Context, onClick: () -> Unit): SwipeHelper.UnderlayButton {
            return SwipeHelper.UnderlayButton(
                context,
                "Delete",
                14.0f,
                Color.RED,
                object : SwipeHelper.UnderlayButtonClickListener {
                    override fun onClick() {
                        onClick()
                    }
                },
            )
        }

    }
}