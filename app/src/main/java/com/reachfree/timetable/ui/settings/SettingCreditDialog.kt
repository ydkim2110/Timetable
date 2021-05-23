package com.reachfree.timetable.ui.settings

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import com.reachfree.timetable.R
import com.reachfree.timetable.data.model.GraduationCreditType
import com.reachfree.timetable.databinding.SettingCreditDialogBinding
import com.reachfree.timetable.extension.longToast
import com.reachfree.timetable.extension.setOnSingleClickListener
import timber.log.Timber

class SettingCreditDialog(
    private val type: GraduationCreditType,
    private val credit: Int
) : DialogFragment() {

    private var _binding: SettingCreditDialogBinding? = null
    private val binding get() = _binding!!

    private lateinit var settingCreditDialogListener: SettingCreditDialogListener

    interface SettingCreditDialogListener {
        fun onPositiveButtonClicked(type: GraduationCreditType, value: Int)
    }

    fun setOnSettingCreditDialogListener(settingCreditDialogListener: SettingCreditDialogListener) {
        this.settingCreditDialogListener = settingCreditDialogListener
    }

    override fun onStart() {
        super.onStart()

        val outMetrics = DisplayMetrics()
        val display: Display?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            display = activity?.display
            display?.getRealMetrics(outMetrics)
        } else {
            @Suppress("DEPRECATION")
            display = activity?.windowManager?.defaultDisplay
            @Suppress("DEPRECATION")
            display?.getRealMetrics(outMetrics)
        }
        val size = Point()
        display?.getRealSize(size)

        dialog?.window?.setLayout((size.x * 0.8).toInt(), LinearLayout.LayoutParams.WRAP_CONTENT)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SettingCreditDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDefaultValue()
        setupViewHandler()
    }

    private fun setupDefaultValue() {
        binding.txtTitle.text = getTitleByType(type)
        binding.txtCurrentCredit.text =
            requireActivity().resources.getString(R.string.text_input_current_credit, credit)
    }

    private fun setupViewHandler() {
        binding.btnCancel.setOnSingleClickListener { dismiss() }

        binding.btnOk.setOnSingleClickListener {
            val creditValue = binding.edtCredit.text.toString().toIntOrNull()
            creditValue?.let {
                if (it <= 0) {
                    requireActivity().longToast(getString(R.string.toast_credit_value_more_than_zero_warning_message))
                    return@setOnSingleClickListener
                }
                settingCreditDialogListener.onPositiveButtonClicked(type, it)
                dismiss()
            } ?: run {
                requireActivity().longToast(getString(R.string.toast_credit_value_warning_message))
            }
        }
    }

    private fun getTitleByType(type: GraduationCreditType): String {
        return when (type) {
            GraduationCreditType.GRADUATION -> requireActivity().resources.getString(R.string.text_graduation)
            GraduationCreditType.MANDATORY -> requireActivity().resources.getString(R.string.text_mandatory)
            GraduationCreditType.ELECTIVE -> requireActivity().resources.getString(R.string.text_elective)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG  = "SettingCreditDialog"
    }

}