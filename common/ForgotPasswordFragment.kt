package com.vrsidekick.fragments.common

import android.content.Context
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.vrsidekick.NavGraphAuthDirections
import com.vrsidekick.R
import com.vrsidekick.activities.prefs
import com.vrsidekick.databinding.FragmentForgotPasswordBinding
import com.vrsidekick.interfaces.IBaseActivity
import com.vrsidekick.utils.setSafeOnClickListener
import com.vrsidekick.viewModels.AuthViewModel


private const val TAG = "ForgotPasswordFragment"

class ForgotPasswordFragment : Fragment() {
    private lateinit var binding: FragmentForgotPasswordBinding

    private val authViewModel: AuthViewModel by viewModels()
    private var mIBaseActivity: IBaseActivity? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListener()

        setupObserver()
    }

    private fun setupObserver() {
        authViewModel.getProgressObserver.observe(viewLifecycleOwner){
            mIBaseActivity?.showProgressDialog(it)

        }

        authViewModel.getMessageObserver.observe(viewLifecycleOwner){
            mIBaseActivity?.showMessage(it)

        }

        authViewModel.getForgotPasswordObserver.observe(viewLifecycleOwner){
            MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialog_App)
                .setTitle("Success")
                .setMessage(it.message)
                .setPositiveButton(getString(R.string.dismiss)){dialog,_ ->
                    dialog.dismiss()
                    findNavController().navigateUp()
                }.show()
        }
    }

    private fun setupListener() {
        binding.ivBack.setSafeOnClickListener(1000) {
            findNavController().navigateUp()
        }

        binding.btResetPassword.setSafeOnClickListener(1000) {
            mIBaseActivity?.hideKeyboard()
            validateForgotPasswordForm()
        }
    }

    private fun validateForgotPasswordForm() {
        val email = binding.etEmail.text.toString().trim()

        if (email.isEmpty()) {
            mIBaseActivity?.showMessage(getString(R.string.messageEmailRequired))
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mIBaseActivity?.showMessage(getString(R.string.messageInvalidEmail))
            return
        }

        authViewModel.forgotPassword(email)
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