package com.vrsidekick.dialogFragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.vrsidekick.R
import com.vrsidekick.activities.prefs
import com.vrsidekick.databinding.FragmentChooseBookingTimingsDialogBinding
import com.vrsidekick.interfaces.IBaseActivity
import com.vrsidekick.utils.LogHelper
import com.vrsidekick.utils.setSafeOnClickListener
import com.vrsidekick.viewModels.ProviderViewModel


private const val TAG ="ChooseBookingDurationDialogFragment"
private const val KEY_PROVIDER_ID = "key_provider_id"
private const val KEY_CATEGORY_ID = "key_category_id"
class ChooseBookingTimingsDialogFragment : BottomSheetDialogFragment() {
    private lateinit var binding : FragmentChooseBookingTimingsDialogBinding
    private val providerViewModel : ProviderViewModel by viewModels()
    private var mIBaseActivity : IBaseActivity? =null
    private var mDateArrayAdapter : ArrayAdapter<String>? =null
    private var mTimeArrayAdapter : ArrayAdapter<String>? =null
    private var mCallback : IChooseBookingTimings? =null
    private var mProviderId:Long? =null
    private var mCategoryId:Long? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mProviderId = it.getLong(KEY_PROVIDER_ID)
            mCategoryId = it.getLong(KEY_CATEGORY_ID)
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChooseBookingTimingsDialogBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObserver()
        setupClickListener()
        getProviderAvailability()



    }


  private  fun  getProviderAvailability(availableDate:String = ""){
        if(mProviderId != null && mCategoryId != null){
            providerViewModel.getProviderAvailability(
                "Bearer ${prefs.userToken}",
                mProviderId!!,
                mCategoryId!!,
                availableDate)

        }
    }



    private fun setupObserver() {
        providerViewModel.getMessageObserver.observe(viewLifecycleOwner){
            mIBaseActivity?.showMessage(it)
        }

        providerViewModel.getProgressObserver.observe(viewLifecycleOwner){
          //  mIBaseActivity?.showProgressDialog(it)
        }

        providerViewModel.getProviderAvailabilityObserver.observe(viewLifecycleOwner){
            mDateArrayAdapter = ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1,it.dates)
            mTimeArrayAdapter = ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1,it.times)
            binding.autocompleteEtDate.setAdapter(mDateArrayAdapter)
            binding.autocompleteEtTime.setAdapter(mTimeArrayAdapter)
            if(it.dates.isNotEmpty()){

                binding.autocompleteEtDate.setText(it.dates.first(),false)
            }
            if(it.times.isNotEmpty()){
                binding.autocompleteEtTime.setText(it.times.first(),false)
            }

        }

    }

    private fun setupClickListener() {
        binding.autocompleteEtDate.setOnItemClickListener { adapterView, view, i, l ->
           getProviderAvailability(adapterView.adapter.getItem(i).toString())
        }

        binding.btSelectContinue.setSafeOnClickListener {
            validateData()

        }

    }

    private fun validateData() {

        val date = binding.autocompleteEtDate.text.toString().trim()
        val time = binding.autocompleteEtTime.text.toString().trim()
        if(date.isEmpty()){
            Toast.makeText(requireContext(), getString(R.string.messageSelectBookingDate), Toast.LENGTH_SHORT).show()
            return
        }

        if(time.isEmpty()){
            Toast.makeText(requireContext(), getString(R.string.messageSelectBookingTime), Toast.LENGTH_SHORT).show()
            return
        }


        mCallback?.onChooseDuration(date, time)
        dismiss()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mIBaseActivity = context as IBaseActivity
    }

    override fun onDetach() {
        super.onDetach()
        mIBaseActivity = null
    }

    companion object {

        @JvmStatic
        fun newInstance(providerId:Long,categoryId:Long) =
            ChooseBookingTimingsDialogFragment().apply {
                arguments = Bundle().apply {
                    putLong(KEY_PROVIDER_ID,providerId)
                    putLong(KEY_CATEGORY_ID,categoryId)
                }
            }

    }


    fun setOnChooseBookingTimingListener(callback : IChooseBookingTimings){
        mCallback = callback
    }



    interface  IChooseBookingTimings{
        fun onChooseDuration(date:String,time:String)
    }
}