package com.parinya.worklog.util

import android.os.Build
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.parinya.worklog.db.Work
import com.parinya.worklog.ui.home.WorkRecyclerViewAdapter
import java.time.LocalDate
import java.time.format.DateTimeFormatter


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
fun TextView.formatDateText(date: String) {
    try {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yy")
        val localDate = LocalDate.parse(date, formatter)

        val formatterV2 = DateTimeFormatter.ofPattern("dd MMMM y")
        this.text = localDate.format(formatterV2)
    } catch (e: java.lang.Exception) {
        this.text = date
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