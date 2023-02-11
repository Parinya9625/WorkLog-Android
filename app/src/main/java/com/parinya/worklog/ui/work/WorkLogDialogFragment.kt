package com.parinya.worklog.ui.work

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class WorkLogDialogFragment(private val myView: View) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity.let {
            val args = arguments

            val builder = MaterialAlertDialogBuilder(it!!)
            builder.apply {
                setView(myView)
            }

            builder.create()
        }
    }

}