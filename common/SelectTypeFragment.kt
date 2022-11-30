package com.vrsidekick.fragments.common

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.vrsidekick.R
import com.vrsidekick.activities.AuthActivity
import com.vrsidekick.activities.BaseActivity
import com.vrsidekick.activities.prefs
import com.vrsidekick.databinding.FragmentSelectTypeBinding
import com.vrsidekick.interfaces.IBaseActivity
import com.vrsidekick.utils.AccountType
import com.vrsidekick.utils.setSafeOnClickListener
import com.vrsidekick.viewModels.AuthViewModel

private const val TAG ="SelectTypeFragment"
class SelectTypeFragment : Fragment() {
    private lateinit var binding : FragmentSelectTypeBinding
    private var mAccountType : AccountType? =null
    private var mIBaseActivity : IBaseActivity? =null
    private val authViewModel : AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSelectTypeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListener()
        setupObserver()
    }

    private fun setupObserver() {
        authViewModel.getProgressObserver.observe(viewLifecycleOwner){
            mIBaseActivity?.showProgressDialog(it)

        }

        authViewModel.getMessageObserver.observe(viewLifecycleOwner){
            mIBaseActivity?.showMessage(it)

        }

        authViewModel.getSetUserTypeObserver.observe(viewLifecycleOwner){
            mAccountType?.let {  type ->
                when(type){
                    AccountType.SIDEKICK_NEEDED -> {
                        prefs.selectedAccountType = type.name
                        (activity as AuthActivity).switchAccount(AccountType.SIDEKICK_NEEDED)
                    }
                    AccountType.SIDEKICK ->{
                        prefs.selectedAccountType = type.name
                        (activity as AuthActivity).switchAccount(AccountType.SIDEKICK)
                    }
                }
            }

        }
    }

    private fun setupClickListener() {
        binding.cardSidekickNeeded.setSafeOnClickListener(1000) {
            mAccountType = AccountType.SIDEKICK_NEEDED
            authViewModel.setAccountType(
                "Bearer ${prefs.userToken}",
                mAccountType!!
            )

        }

        binding.cardSidekick.setSafeOnClickListener(1000) {
            mAccountType =AccountType.SIDEKICK
            authViewModel.setAccountType(
                "Bearer ${prefs.userToken}",
                mAccountType!!
            )
        }
    }


    override fun onStop() {
        super.onStop()
        mIBaseActivity?.showProgressDialog(false)
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