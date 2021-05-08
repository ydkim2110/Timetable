package com.reachfree.timetable.ui.setup

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.DatePicker
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import java.util.*

class DatePickerFragment: DialogFragment(),
    DatePickerDialog.OnDateSetListener,
    DialogInterface.OnShowListener {

    var dateSelected: ((Int, Int, Int, String) -> Unit)? = null

    private lateinit var type: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        type = requireArguments().getString(TYPE, SetupActivity.START_TIME)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val dayOfMonth = cal.get(Calendar.DAY_OF_MONTH)
        val dialog = DatePickerDialog(requireActivity(), this, year, month, dayOfMonth)
        dialog.setOnShowListener(this)
        return dialog
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        dateSelected?.invoke(year, month, dayOfMonth, type)
    }

    override fun onShow(dialog: DialogInterface?) {
        (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark))
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            .setTextColor(ContextCompat.getColor(requireContext(), android.R.color.darker_gray))
    }

    companion object {
        const val TAG = "DatePickerFragment"
        private const val TYPE = "type"

        fun newInstance(type: String) = DatePickerFragment().apply {
            arguments = bundleOf(TYPE to type)
        }
    }


}