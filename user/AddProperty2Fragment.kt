package com.vrsidekick.fragments.user

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.vrsidekick.R
import com.vrsidekick.activities.BaseActivity
import com.vrsidekick.activities.UserActivity
import com.vrsidekick.activities.prefs
import com.vrsidekick.databinding.FragmentAddProperty2Binding
import com.vrsidekick.interfaces.IBaseActivity
import com.vrsidekick.utils.setSafeOnClickListener
import com.vrsidekick.viewModels.PropertyViewModel

private const val TAG = "AddProperty2Fragment"

class AddProperty2Fragment : Fragment() {
    private lateinit var binding: FragmentAddProperty2Binding
    private val propertyViewModel: PropertyViewModel by activityViewModels()
    private var mIBaseActivity : IBaseActivity? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddProperty2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as BaseActivity).isShowBottomNavBar()
        setupClickListener()
        setupObserver()
        setupFormData()
    }


    private fun setupFormData() {
        if(propertyViewModel.isEditProperty){
            val uploadPropertyData = propertyViewModel.getPropertyData()

            binding.etNumberOfFloors.setText(uploadPropertyData.numFloors?:"")
            binding.etNumberOfBedrooms.setText(uploadPropertyData.numBedrooms?:"")
            binding.etNumberOfBeds.setText(uploadPropertyData.numBeds?:"")
            binding.etNumberOfSleeps.setText(uploadPropertyData.sleepsNum?:"")
            binding.etLink.setText(uploadPropertyData.iCalLink)

            uploadPropertyData.propertyOptions?.let {
                if(it == "0"){
                    binding.rbPrivate.isChecked = true
                }else{
                    binding.rbShared.isChecked =true
                }

            }
        }

    }

    private fun setupObserver() {
        propertyViewModel.getProgressObserver.observe(viewLifecycleOwner){
            mIBaseActivity?.showProgressDialog(it)
        }
        propertyViewModel.getMessageObserver.observe(viewLifecycleOwner){
            mIBaseActivity?.showMessage(it)

        }
        propertyViewModel.getAddPropertyObserver.observe(viewLifecycleOwner){

            MaterialAlertDialogBuilder(requireContext(),R.style.MaterialAlertDialog_App)
                .setCancelable(false)
                .setTitle(getString(R.string.success))
                .setMessage(it.message)
                .setPositiveButton(getString(R.string.dismiss)){dialog,_ ->
                    dialog.dismiss()
                    requireActivity().viewModelStore.clear()
                   // findNavController().popBackStack(R.id.postAdSelectPropertyFragment,false)
                    findNavController().popBackStack()
                    findNavController().popBackStack()
                }
                .show()

        }



    }

    private fun setupClickListener() {
        binding.ivBack.setSafeOnClickListener(1000) {
            it.findNavController().navigateUp()
        }

        binding.btAddProperty.setSafeOnClickListener {
            mIBaseActivity?.hideKeyboard()
            validateFormData()
        }
    }

    private fun validateFormData() {

        val numFloors = binding.etNumberOfFloors.text.toString().trim()
        val numBedrooms = binding.etNumberOfBedrooms.text.toString().trim()
        val numBeds = binding.etNumberOfBeds.text.toString().trim()
        val sleepsNum = binding.etNumberOfSleeps.text.toString().trim()
        val iCalLink = binding.etLink.text.toString().trim();
        val propertyOption = getPropertyOption()



        if(numFloors.isEmpty()){
            mIBaseActivity?.showMessage(getString(R.string.messageEnterNumOfFloors))
            return
        }


        if(numBedrooms.isEmpty()){
            mIBaseActivity?.showMessage(getString(R.string.messageEnterNumOfBedrooms))
            return
        }

        if(numBeds.isEmpty()){
            mIBaseActivity?.showMessage(getString(R.string.messageEnterNumOfBeds))
            return
        }

        if(sleepsNum.isEmpty()){
            mIBaseActivity?.showMessage(getString(R.string.messageEnterSleepsNumber))
            return
        }

        if(iCalLink.isEmpty()){
            mIBaseActivity?.showMessage(getString(R.string.messageAddLink))
            return
        }


        val uploadPropertyData = propertyViewModel.getPropertyData()

        uploadPropertyData.numFloors = numFloors
        uploadPropertyData.numBedrooms = numBedrooms
        uploadPropertyData.numBeds = numBeds
        uploadPropertyData.sleepsNum = sleepsNum
        uploadPropertyData.propertyOptions = propertyOption
        uploadPropertyData.iCalLink = iCalLink


        Log.d(TAG, "validateFormData: Add Property $uploadPropertyData")

        propertyViewModel.addProperty(
            "Bearer ${prefs.userToken}",
        )
    }

    private fun getPropertyOption(): String {
        val checkedId = binding.rgPropertyOptions.checkedRadioButtonId
        return if (checkedId == binding.rbShared.id) "1" else "0"
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mIBaseActivity = context as IBaseActivity
    }

    override fun onDetach() {
        super.onDetach()
        mIBaseActivity = null
    }
}