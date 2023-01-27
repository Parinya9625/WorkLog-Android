package com.parinya.worklog.util

import android.os.Build
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.parinya.worklog.db.Work
import com.parinya.worklog.ui.home.WorkRecyclerViewAdapter


@BindingAdapter("items")
fun RecyclerView.items(items: List<Work>?) {
    val adapter = getAdapter(this)
    if (items != null) {
        adapter.updateWorks(items)
    }
}

fun getAdapter(recyclerView: RecyclerView): WorkRecyclerViewAdapter {
    if (recyclerView.adapter != null && recyclerView.adapter is WorkRecyclerViewAdapter) {
        return recyclerView.adapter as WorkRecyclerViewAdapter
    } else {
        val adapter = WorkRecyclerViewAdapter()
        recyclerView.adapter = adapter
        return adapter
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@BindingAdapter("epochSecToString")
fun TextView.epochSecToDateString(sec: Long) {
    this.text = Util.epochSecToDateString(sec)
}

@RequiresApi(Build.VERSION_CODES.O)
@BindingAdapter("formatDateText")
fun TextView.formatDateText(dateInMS: Long) {
    try {
        this.text = Util.dateToString(dateInMS)
    } catch (e: java.lang.Exception) {
        this.text = dateInMS.toString()
    }
}

@BindingAdapter("autoHide")
fun View.autoHide(value: String) {
    if (value.isBlank()) {
        this.visibility = View.GONE
    } else {
        this.visibility = View.VISIBLE
    }
}