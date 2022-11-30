package com.vrsidekick.dialogFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.vrsidekick.activities.prefs
import com.vrsidekick.adapter.user.MyPropertiesFilterAdapter
import com.vrsidekick.databinding.FragmentMyPropertiesFilterDialogBinding
import com.vrsidekick.viewModels.PropertyViewModel

private const val TAG= "MyPropertiesDialogFragment"
class MyPropertiesFilterDialogFragment : BottomSheetDialogFragment(),
    MyPropertiesFilterAdapter.IMyPropertiesFilterAdapter {
   private lateinit var binding : FragmentMyPropertiesFilterDialogBinding
   private val propertyViewModel : PropertyViewModel by viewModels()
    private var mMyPropertyFilterAdapter : MyPropertiesFilterAdapter? =null
    private var mCallback : IMyPropertiesDialogFragment? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mMyPropertyFilterAdapter = MyPropertiesFilterAdapter()
        mMyPropertyFilterAdapter?.setOnMyPropertiesFilterClickListener(this)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyPropertiesFilterDialogBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvMyProperties.adapter = mMyPropertyFilterAdapter
        setupPropertyObserver()

        propertyViewModel.getMyProperties(
            "Bearer ${prefs.userToken}"
        )

    }

    private fun setupPropertyObserver() {
        propertyViewModel.getMessageObserver.observe(viewLifecycleOwner){
            Toast.makeText(requireContext(), "it", Toast.LENGTH_SHORT).show()
        }

        propertyViewModel.getProgressObserver.observe(viewLifecycleOwner){
            binding.progressBar.visibility = if(it) View.VISIBLE else View.GONE
        }

        propertyViewModel.getMyPropertyObserver.observe(viewLifecycleOwner){
            mMyPropertyFilterAdapter?.submitList(it)
        }



    }

    override fun onStop() {
        super.onStop()
        binding.progressBar.visibility = View.GONE
    }

    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() = MyPropertiesFilterDialogFragment()

    }

    override fun onItemClick(propertyId: Long) {

        mCallback?.onPropertyClick(propertyId)
        dismiss()

    }


   fun setOnMyPropertiesClickListener(callback : IMyPropertiesDialogFragment){
       mCallback = callback
   }


    interface  IMyPropertiesDialogFragment{
        fun onPropertyClick(propertyId:Long)
    }
}