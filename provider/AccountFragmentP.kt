package com.vrsidekick.fragments.provider

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.vrsidekick.NavGraphProviderDirections
import com.vrsidekick.R
import com.vrsidekick.activities.BaseActivity
import com.vrsidekick.activities.ProviderActivity
import com.vrsidekick.activities.UserActivity
import com.vrsidekick.activities.prefs
import com.vrsidekick.databinding.FragmentAccountPBinding
import com.vrsidekick.interfaces.IBaseActivity
import com.vrsidekick.utils.AccountType
import com.vrsidekick.utils.loadFromUrl
import com.vrsidekick.utils.setSafeOnClickListener
import com.vrsidekick.viewModels.AuthViewModel

private const val TAG= "AccountFragmentP"
class AccountFragmentP : Fragment() {
    private lateinit var binding : FragmentAccountPBinding
    private var mIBaseActivity : IBaseActivity? =null
    private val authViewModel : AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountPBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as BaseActivity).isShowBottomNavBar(true)
        binding.ivUser.loadFromUrl(prefs.currentUser?.profilePhotoUrl)
        binding.tvUserName.text = prefs.currentUser?.name
        setupClickListener()

        setupAccountChangeListener()
        setupLogoutObserver()
    }




    private fun setupClickListener() {
        binding.btProfile.setSafeOnClickListener(1000) {
            val direction = AccountFragmentPDirections.actionNavAccountProviderToProfileFragmentP()
            it.findNavController().navigate(direction)
        }

        binding.btChangePassword.setSafeOnClickListener(1000) {
            val direction = AccountFragmentPDirections.actionNavAccountProviderToChangePasswordFragment()
            it.findNavController().navigate(direction)
        }

        binding.btNotification.setSafeOnClickListener(1000) {
            val direction = AccountFragmentPDirections.actionNavAccountProviderToNotificationFragmentP()
            it.findNavController().navigate(direction)
        }

        binding.btPayment.setSafeOnClickListener(1000) {
            val direction = AccountFragmentPDirections.actionNavAccountProviderToAddPaymentAccountFragmentP()
            it.findNavController().navigate(direction)
        }

        binding.btLogout.setSafeOnClickListener {
            showLogoutAlert()
        }

    }


    private fun setupLogoutObserver() {
        authViewModel.getProgressObserver.observe(viewLifecycleOwner){
            mIBaseActivity?.showProgressDialog(it)

        }

        authViewModel.getMessageObserver.observe(viewLifecycleOwner){
            mIBaseActivity?.showMessage(it)

        }

        authViewModel.getLogoutObserver.observe(viewLifecycleOwner){
            mIBaseActivity?.logoutUser(requireActivity() as ProviderActivity)
        }

    }









    private fun showLogoutAlert() {
        MaterialAlertDialogBuilder(requireContext(),R.style.MaterialAlertDialog_App)
            .setTitle(getString(R.string.logout))
            .setMessage(getString(R.string.messageLogout))
            .setPositiveButton(getString(R.string.dismiss)){dialog,which ->
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.logout)){dialog,which ->
                dialog.dismiss()


                val token = prefs.userToken
                token?.let {
                    authViewModel.logoutUser("Bearer $it") }?: mIBaseActivity?.logoutUser(requireActivity() as ProviderActivity)


            }.show()
    }


    private fun setupAccountChangeListener() {
        binding.switchToggleProfile.isChecked = false
        binding.switchToggleProfile.setOnCheckedChangeListener { compoundButton, b ->

            if(b){
                showChangeAccountAlert()
            }


        }

    }

    private fun showChangeAccountAlert() {
        MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialog_App)
            .setTitle(getString(R.string.switchAccount))
            .setMessage("Do you want to Switch account?")
            .setPositiveButton("Switch"){dialog,which ->
                dialog.dismiss()
                prefs.selectedAccountType = AccountType.SIDEKICK_NEEDED.name
                (activity as BaseActivity).switchAccount(AccountType.SIDEKICK_NEEDED)
            }
            .setNegativeButton("Cancel"){dialog,which ->

                dialog.dismiss()
                binding.switchToggleProfile.isChecked=  false

            }
            .show()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mIBaseActivity = context as IBaseActivity
    }

    override fun onDetach() {
        super.onDetach()
        mIBaseActivity =null
    }
}