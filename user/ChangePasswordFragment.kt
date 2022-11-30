package com.vrsidekick.fragments.user

import android.content.Context
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.vrsidekick.R
import com.vrsidekick.R.string.messageConfirmPasswordRequired
import com.vrsidekick.activities.BaseActivity
import com.vrsidekick.activities.UserActivity
import com.vrsidekick.activities.prefs
import com.vrsidekick.databinding.FragmentChangePasswordBinding
import com.vrsidekick.interfaces.IBaseActivity
import com.vrsidekick.utils.Global
import com.vrsidekick.utils.setSafeOnClickListener
import com.vrsidekick.viewModels.AuthViewModel

private const val TAG = "ChangePasswordFragment"

class ChangePasswordFragment : Fragment() {
    private lateinit var binding: FragmentChangePasswordBinding
    private val authViewModel: AuthViewModel by viewModels()
    private var mIBaseActivity: IBaseActivity? = null
    private var currentPasswordVisibility: Boolean = false
    private var newPasswordVisibility: Boolean = false
    private var confirmPasswordVisibility: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as BaseActivity).isShowBottomNavBar()
        setupClickListener()
        setupObserver()
    }

    private fun setupObserver() {
        authViewModel.getProgressObserver.observe(viewLifecycleOwner) {
            mIBaseActivity?.showProgressDialog(it)

        }
        authViewModel.getMessageObserver.observe(viewLifecycleOwner) {
            mIBaseActivity?.showMessage(it)

        }
        authViewModel.getChangePasswordObserver.observe(viewLifecycleOwner) {
            MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialog_App)
                .setTitle(getString(R.string.success))
                .setMessage(it.message)
                .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                    dialog.dismiss()
                    findNavController().navigateUp()
                }
                .show()

        }
    }

    private fun setupClickListener() {
        binding.ivBack.setSafeOnClickListener(1000) {
            it.findNavController().navigateUp()
        }

        binding.btSave.setSafeOnClickListener {

            validateChangePasswordForm()
        }

        binding.ivToggleCurrentPasswordVisibility.setOnClickListener {
            togglePassword(0)
        }

        binding.ivToggleNewPasswordVisibility.setOnClickListener {
            togglePassword(1)
        }

        binding.ivToggleConfirmPasswordVisibility.setOnClickListener {
            togglePassword(2)
        }
    }

    private fun validateChangePasswordForm() {
        mIBaseActivity?.hideKeyboard()
        val currentPassword = binding.etCurrentPassword.text.toString().trim()
        val newPassword = binding.etNewPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()

        if (currentPassword.isEmpty()) {
            mIBaseActivity?.showMessage(getString(R.string.messageCurrentPasswordRequired))
            return
        }

        if (newPassword.isEmpty()) {
            mIBaseActivity?.showMessage(getString(R.string.messagePasswordRequired))
            return
        }

        if (confirmPassword.isEmpty()) {
            mIBaseActivity?.showMessage(getString(messageConfirmPasswordRequired))
            return
        }

        authViewModel.changePassword(
            "Bearer ${prefs.userToken}",
            currentPassword, newPassword, confirmPassword
        )
    }


    private fun togglePassword(type: Int) {
        if (type == 0) {
            if (currentPasswordVisibility) {
                currentPasswordVisibility = false
                binding.ivToggleCurrentPasswordVisibility.setImageResource(R.drawable.ic_eye_hide)
                binding.etCurrentPassword.transformationMethod = PasswordTransformationMethod()
            } else {
                currentPasswordVisibility = true
                binding.ivToggleCurrentPasswordVisibility.setImageResource(R.drawable.ic_eye)
                binding.etCurrentPassword.transformationMethod = null
            }
        } else if (type == 1) {
            if (newPasswordVisibility) {
                newPasswordVisibility = false
                binding.ivToggleNewPasswordVisibility.setImageResource(R.drawable.ic_eye_hide)
                binding.etNewPassword.transformationMethod = PasswordTransformationMethod()
            } else {
                newPasswordVisibility = true
                binding.ivToggleNewPasswordVisibility.setImageResource(R.drawable.ic_eye)
                binding.etNewPassword.transformationMethod = null
            }

        } else if (type == 2) {
            if (confirmPasswordVisibility) {
                confirmPasswordVisibility = false
                binding.ivToggleConfirmPasswordVisibility.setImageResource(R.drawable.ic_eye_hide)
                binding.etConfirmPassword.transformationMethod = PasswordTransformationMethod()
            } else {
                confirmPasswordVisibility = true
                binding.ivToggleConfirmPasswordVisibility.setImageResource(R.drawable.ic_eye)
                binding.etConfirmPassword.transformationMethod = null
            }

        }

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