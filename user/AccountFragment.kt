package com.vrsidekick.fragments.user

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.vrsidekick.NavGraphAuthDirections
import com.vrsidekick.NavGraphDirections
import com.vrsidekick.R
import com.vrsidekick.activities.AuthActivity
import com.vrsidekick.activities.BaseActivity
import com.vrsidekick.activities.UserActivity
import com.vrsidekick.activities.prefs
import com.vrsidekick.databinding.FragmentAccountBinding
import com.vrsidekick.interfaces.IBaseActivity
import com.vrsidekick.utils.AccountType
import com.vrsidekick.utils.loadFromUrl
import com.vrsidekick.utils.setSafeOnClickListener
import com.vrsidekick.viewModels.AccountViewModel
import com.vrsidekick.viewModels.AuthViewModel

private const val TAG= "AccountFragment"
class AccountFragment : Fragment() {
    private lateinit var binding : FragmentAccountBinding
    private var mIBaseActivity : IBaseActivity? =null
    private val authViewModel : AuthViewModel by viewModels()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as BaseActivity).isShowBottomNavBar(true)
        binding.ivUser.loadFromUrl(prefs.currentUser?.profilePhotoUrl)
        binding.ivUser.setOnClickListener {
            val direction = prefs.currentUser?.profilePhotoUrl?.let { it1 ->
                NavGraphDirections.actionGlobalViewFullImageProviderProfileFragment(
                    arrayOf((it1))
                )
            }
            findNavController().navigate(direction!!)

        }
        binding.tvUserName.text = prefs.currentUser?.name
        setupClickListener()
        setupAccountChangeListener()
        setupLogoutObserver()
    }



    private fun setupLogoutObserver() {
        authViewModel.getProgressObserver.observe(viewLifecycleOwner){
            mIBaseActivity?.showProgressDialog(it)

        }

        authViewModel.getMessageObserver.observe(viewLifecycleOwner){
            mIBaseActivity?.showMessage(it)

        }

        authViewModel.getLogoutObserver.observe(viewLifecycleOwner){
            mIBaseActivity?.logoutUser(requireActivity() as UserActivity)
        }

    }





    private fun setupClickListener() {
        binding.btProfile.setSafeOnClickListener {
            val direction = AccountFragmentDirections.actionNavAccountToProfileFragment()
            findNavController().navigate(direction)
        }

        binding.btMyCards.setSafeOnClickListener {
            val direction = NavGraphDirections.actionGlobalCardListFragment()
            findNavController().navigate(direction)
        }

        binding.btNotification.setSafeOnClickListener {
            val direction = AccountFragmentDirections.actionNavAccountToNotificationFragment()
            findNavController().navigate(direction)
        }

        binding.btChangePassword.setSafeOnClickListener {
            val direction = AccountFragmentDirections.actionNavAccountToChangePasswordFragment()
            findNavController().navigate(direction)
        }

        binding.btLogout.setSafeOnClickListener {
            showLogoutAlert()
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
                mIBaseActivity?.logoutUser(requireActivity() as UserActivity)
               /* val token = prefs.userToken
                token?.let {
                    authViewModel.logoutUser("Bearer $it")
                }?: mIBaseActivity?.logoutUser(requireActivity() as UserActivity)*/




            }
            .show()
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
                prefs.selectedAccountType = AccountType.SIDEKICK.name
                (activity as BaseActivity).switchAccount(AccountType.SIDEKICK)
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