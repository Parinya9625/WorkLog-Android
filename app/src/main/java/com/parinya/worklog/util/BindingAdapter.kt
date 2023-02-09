package com.parinya.worklog.util

import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import com.parinya.worklog.R
import com.parinya.worklog.db.work.Work
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

@BindingAdapter("enableClearButton")
fun TextInputLayout.enableClearButton(isEnable: Boolean) {

    fun setClearButton() {
        this@enableClearButton.endIconDrawable = ResourcesCompat.getDrawable(resources, R.drawable.ic_clear_24, null)
        this.setEndIconOnClickListener {
            this.editText?.text?.clear()
        }
    }
    fun removeClearButton() {
        this@enableClearButton.endIconDrawable = null
        this.setEndIconOnClickListener { }
    }

    if (isEnable) {
        this.endIconMode = TextInputLayout.END_ICON_CUSTOM

        this.editText?.apply {

            val textWatcher = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    setClearButton()
                }
            }

            setOnFocusChangeListener { view, isFocus ->
                if (isFocus) {
                    this.addTextChangedListener(textWatcher)
                    if (this.text.toString().isNotBlank()) {
                        setClearButton()
                    }
                } else {
                    this.removeTextChangedListener(textWatcher)
                    removeClearButton()
                }
            }
        }
    }
}