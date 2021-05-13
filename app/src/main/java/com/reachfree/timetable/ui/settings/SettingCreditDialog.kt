package com.reachfree.timetable.ui.settings

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.reachfree.timetable.R
import com.reachfree.timetable.data.model.GraduationCreditType
import com.reachfree.timetable.databinding.SettingCreditDialogBinding
import com.reachfree.timetable.extension.setOnSingleClickListener

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
        val windowManager = requireActivity().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)

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
        binding.btnOk.setOnSingleClickListener {
            val creditValue = binding.edtCredit.text.toString().toIntOrNull()
            creditValue?.let {
                if (it <= 0) {
                    Toast.makeText(requireActivity(), "0보다 큰 값을 입력해주세요.",
                        Toast.LENGTH_LONG).show()
                    return@setOnSingleClickListener
                }
                settingCreditDialogListener.onPositiveButtonClicked(type, it)
                dismiss()
            } ?: Toast.makeText(requireActivity(), "값을 입력해주세요.", Toast.LENGTH_LONG).show()
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