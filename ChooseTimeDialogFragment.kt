package com.vrsidekick.dialogFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.vrsidekick.R
import com.vrsidekick.databinding.FragmentChooseTimeDialogBinding
import com.vrsidekick.fragments.provider.ServiceTimeType
import com.vrsidekick.utils.Global
import com.vrsidekick.utils.SizeConfig
import com.vrsidekick.utils.setSafeOnClickListener
import java.text.SimpleDateFormat
import java.util.*


private const val TAG ="ChooseTimeDialogFragment"
class ChooseTimeDialogFragment : DialogFragment() {

    private lateinit var binding : FragmentChooseTimeDialogBinding
    private var mCallback : IChooseTime? =null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentChooseTimeDialogBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListener()
    }

    private fun setupClickListener() {
        binding.tvStartTime.setSafeOnClickListener(1000) {
            selectTime(ServiceTimeType.START)
        }

        binding.tvEndTime.setSafeOnClickListener(1000) {
            selectTime(ServiceTimeType.END)
        }

        binding.btSubmit.setSafeOnClickListener {
            validateForm()
        }
    }

    private fun validateForm() {
        val startTime = binding.tvStartTime.text.toString().trim()
        val endTime = binding.tvEndTime.text.toString().trim()

        if(startTime.isEmpty()){
            Global.showMessage(binding.root,getString(R.string.messageEnterServiceStartTime))
            return

        }

        if(endTime.isEmpty()){
            Global.showMessage(binding.root,getString(R.string.messageEnterServiceEndTime))
            return
        }

        mCallback?.onSelectTime(startTime, endTime)
        dismiss()

    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(SizeConfig.screenWidth -100,WindowManager.LayoutParams.WRAP_CONTENT)
        dialog?.window?.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.bg_dialog_round_16))
    }

    private fun selectTime(type: ServiceTimeType) {
        val calendar = Calendar.getInstance()
        if(type == ServiceTimeType.START){
            calendar.set(Calendar.HOUR_OF_DAY , 8)
            calendar.set(Calendar.MINUTE , 0)
        }else{
            calendar.set(Calendar.HOUR_OF_DAY , 19)
            calendar.set(Calendar.MINUTE , 0)

        }

        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)

            .setHour(calendar.get(Calendar.HOUR_OF_DAY))
            .setMinute(calendar.get(Calendar.MINUTE))
            .setTitleText(getString(R.string.selectServiceTime))
            .build()

        timePicker.show(childFragmentManager, TAG)
        timePicker.addOnPositiveButtonClickListener {

            val hour = if (timePicker.hour < 10) "0${timePicker.hour}" else timePicker.hour
            val minutes = if (timePicker.minute < 10) "0${timePicker.minute}" else timePicker.minute

            val tempTime = "$hour:$minutes"
            val sdf1 = SimpleDateFormat("HH:mm", Locale.getDefault()).parse(tempTime)
            val result = SimpleDateFormat("hh:mm aa", Locale.getDefault()).format(sdf1)

            if (type == ServiceTimeType.START) {
                binding.tvStartTime.text = result
            } else {
                binding.tvEndTime.text = result
            }
        }
        timePicker.addOnNegativeButtonClickListener {

        }
        timePicker.addOnCancelListener {
            // call back code
        }
        timePicker.addOnDismissListener {
            // call back code
        }

    }


    fun setChooseTimeClickListener(callback : IChooseTime){
        mCallback = callback
    }

    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() = ChooseTimeDialogFragment()
    }

    interface  IChooseTime{
        fun onSelectTime(startTime:String,endTime:String)
    }
}