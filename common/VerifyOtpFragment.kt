package com.vrsidekick.fragments.common

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.vrsidekick.NavGraphAuthDirections
import com.vrsidekick.R
import com.vrsidekick.activities.AuthActivity
import com.vrsidekick.activities.prefs
import com.vrsidekick.databinding.FragmentVerifyOtpBinding
import com.vrsidekick.interfaces.IBaseActivity
import com.vrsidekick.utils.AccountType
import com.vrsidekick.utils.setSafeOnClickListener
import com.vrsidekick.viewModels.AuthViewModel


private const val TAG = "VerifyOtpFragment"

class VerifyOtpFragment : Fragment() {
    private lateinit var binding: FragmentVerifyOtpBinding
    private val navArgs: VerifyOtpFragmentArgs by navArgs()
    private var mIBaseActivity: IBaseActivity? = null
    private val authViewModel: AuthViewModel by viewModels()
    private var mServerOtp: Int = 0


    private val otpResendTimer = object : CountDownTimer(9000, 1000) {

        override fun onTick(millisUntilFinished: Long) {
            binding.tvTimer.text = "00:0${(millisUntilFinished / 1000) + 1}"
            //here you can have your logic to set text to edittext
        }

        override fun onFinish() {
            cancelOtpTimer()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mServerOtp = navArgs.otp


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentVerifyOtpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val messageCodeSendOn = "Code is sent to ${navArgs.phoneCode}${navArgs.phoneNumber}"
        binding.tvCodeSendTo.text = messageCodeSendOn
        startOtpTimer()
        setupClickListener()
        setupObserver()
    }


    private fun setupClickListener() {
        binding.ivBack.setSafeOnClickListener(1000) {
            findNavController().navigateUp()
        }

        binding.btSendAgain.setSafeOnClickListener {
            authViewModel.sendOtp(navArgs.phoneCode, navArgs.phoneNumber)
            mIBaseActivity?.showMessage(getString(R.string.messageOtpSent))
            startOtpTimer()
        }

        binding.otpView.setOtpCompletionListener {
            validateOtp(it)
        }
    }

    private fun validateOtp(otp: String?) {
        mIBaseActivity?.hideKeyboard()
        if (otp == mServerOtp.toString()) {







            authViewModel.registerUser(
                navArgs.name,
                navArgs.email,
                navArgs.password,
                navArgs.phoneCode,
                navArgs.phoneNumber
            )
        } else {
            mIBaseActivity?.showMessage(getString(R.string.messageInvalidOtp))
        }

    }


    private fun setupObserver() {
        authViewModel.getProgressObserver.observe(viewLifecycleOwner) {
            mIBaseActivity?.showProgressDialog(it)

        }

        authViewModel.getMessageObserver.observe(viewLifecycleOwner) {
            mIBaseActivity?.showMessage(it)

        }

        authViewModel.getSendOtpObserver.observe(viewLifecycleOwner) {
            mServerOtp = it.data


        }


        /*{"success":true,"message":"User registered successfully."}  */

        authViewModel.getRegisterObserver.observe(viewLifecycleOwner) {
            MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialog_App)
                .setTitle(getString(R.string.success))
                .setMessage(it.message)
                .setPositiveButton(getString(R.string.ok)) { dialog, which ->
                    dialog.dismiss()
                    findNavController().popBackStack(R.id.loginFragment,false)
                }
                .show()


        }


    }


    private fun cancelOtpTimer() {
        otpResendTimer.cancel()
        binding.btSendAgain.visibility = View.VISIBLE
        binding.tvTimer.visibility = View.INVISIBLE
    }


    private fun startOtpTimer() {
        otpResendTimer.start()
        binding.btSendAgain.visibility = View.INVISIBLE
        binding.tvTimer.visibility = View.VISIBLE
    }


    override fun onStop() {
        super.onStop()
        cancelOtpTimer()
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