package com.vrsidekick.fragments.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.vrsidekick.activities.UserActivity
import com.vrsidekick.R
import com.vrsidekick.activities.BaseActivity
import com.vrsidekick.databinding.FragmentSidekickBinding
import com.vrsidekick.utils.setSafeOnClickListener

private const val TAG= "SidekickFragment"
class SidekickFragment : Fragment() {
   private lateinit var binding : FragmentSidekickBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentSidekickBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as BaseActivity).isShowBottomNavBar(true)
        setupClickListener()
    }

    private fun setupClickListener() {
        binding.btViewMyProperties.setSafeOnClickListener(1000) {
            val direction = SidekickFragmentDirections.actionNavSidekickToMyPropertiesFragment()
            it.findNavController().navigate(direction)
        }

        binding.btMySidekics.setSafeOnClickListener(1000) {
            val direction = SidekickFragmentDirections.actionNavSidekickToMySideKicksFragment()
            it.findNavController().navigate(direction)
        }

        binding.btSidekicsWantedAds.setSafeOnClickListener(1000) {
           showAdOptionsDialog()
        }

        binding.btFavourite.setSafeOnClickListener(1000) {
            val direction = SidekickFragmentDirections.actionNavSidekickToFavouriteFragment()
            it.findNavController().navigate(direction)
        }

        binding.btReporting.setSafeOnClickListener(1000) {
            val direction = SidekickFragmentDirections.actionNavSidekickToReportingFragment()
            it.findNavController().navigate(direction)
        }

    }

    private fun showAdOptionsDialog() {

        MaterialAlertDialogBuilder(requireContext(),R.style.MaterialAlertDialog_App)
            .setItems(arrayOf("View Ad","Wanted Ad")){dialog,which ->
                dialog.dismiss()
            }
            .show()
    }


}