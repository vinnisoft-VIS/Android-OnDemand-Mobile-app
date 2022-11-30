package com.vrsidekick.dialogs

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.vrsidekick.databinding.FragmentAddPropertyOptionsDialogBinding
import com.vrsidekick.utils.setSafeOnClickListener

private const val TAG = "AddPropertyOptionsDialogFragment"


enum class AddPropertyType{
    TYPE_PROPERTY ,TYPE_AD
}

class AddPropertyOptionsDialogFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentAddPropertyOptionsDialogBinding
    private var mCallback: IAddPostOptions? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddPropertyOptionsDialogBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListener()
    }



    override fun onStart() {
        super.onStart()
       // dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.GREEN))
    }

    private fun setupClickListener() {
        binding.cardAddProperty.setSafeOnClickListener {

            mCallback?.onSelectOption(AddPropertyType.TYPE_PROPERTY)
            dismiss()


        }
        binding.cardAddPropertyAd.setSafeOnClickListener {
            mCallback?.onSelectOption(AddPropertyType.TYPE_AD)
            dismiss()

        }
    }


    fun setOnOptionClickListener(callback: IAddPostOptions) {
        mCallback = callback
    }


    interface IAddPostOptions {
        fun onSelectOption(addPropertyType: AddPropertyType)
    }

    companion object {
        fun newInstance() = AddPropertyOptionsDialogFragment()
    }
}