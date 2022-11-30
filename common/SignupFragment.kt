package com.vrsidekick.fragments.common

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.method.PasswordTransformationMethod
import android.text.method.TransformationMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.vrsidekick.R
import com.vrsidekick.activities.AuthActivity
import com.vrsidekick.activities.prefs
import com.vrsidekick.databinding.FragmentSignupBinding
import com.vrsidekick.interfaces.IBaseActivity
import com.vrsidekick.models.SendOtpResModel
import com.vrsidekick.utils.AccountType
import com.vrsidekick.utils.Global
import com.vrsidekick.utils.setSafeOnClickListener
import com.vrsidekick.viewModels.AuthViewModel


private const val TAG = "SignupFragment"

class SignupFragment : Fragment() {
    private lateinit var binding: FragmentSignupBinding
    private var     mIBaseActivity: IBaseActivity? = null
    private val authViewModel: AuthViewModel by viewModels()

    private var isPasswordVisible: Boolean = false
    private var isConfirmPasswordVisible: Boolean = false


    private var name: String? = null
    private var email: String? = null
    private var phoneCode: String? = "+91"
    private var phoneNumber: String? = null
    private var password: String? = null
    private var confirmPassword: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListener()
        setupLoginSpan()
        setupObserver()
    }


    private fun setupLoginSpan() {
        val ss = SpannableString(getString(R.string.alreadyHaveAccount))
        ss.setSpan(StyleSpan(Typeface.BOLD), 24, ss.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        ss.setSpan(object : ClickableSpan() {
            override fun onClick(p0: View) {
                findNavController().navigateUp()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = Color.WHITE
                ds.isUnderlineText = false
            }
        }, 24, ss.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tvAlreadyHaveAccount.text = ss
        binding.tvAlreadyHaveAccount.movementMethod = LinkMovementMethod.getInstance()
        binding.tvAlreadyHaveAccount.highlightColor = Color.TRANSPARENT
    }

    private fun setupListener() {
        binding.btSignUp.setSafeOnClickListener {
            validateSignupForm()
        }

        binding.btSignUp.setSafeOnClickListener(1000) {
           mIBaseActivity?.hideKeyboard()
           validateSignupForm()
        }


        binding.ivTogglePasswordVisibility.setOnClickListener {
            togglePassword(0)
        }

        binding.ivToggleConfirmPasswordVisibility.setOnClickListener {
            togglePassword(1)
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

            moveToVerifyOtp(it)

        }

    }

    private fun moveToVerifyOtp(otpRes: SendOtpResModel) {

        val direction = SignupFragmentDirections.actionSignupFragmentToVerifyOtpFragment(
            name!!,
            email!!,
            phoneCode!!,
            phoneNumber!!,
            password!!,
            otpRes.data
        )

        findNavController().navigate(direction)

    }

    private fun togglePassword(type: Int) {
        if (type == 0) {
            if (isPasswordVisible) {
                isPasswordVisible = false
                binding.ivTogglePasswordVisibility.setImageResource(R.drawable.ic_eye_hide)
                binding.etPassword.transformationMethod = PasswordTransformationMethod()
            } else {
                isPasswordVisible = true
                binding.ivTogglePasswordVisibility.setImageResource(R.drawable.ic_eye)
                binding.etPassword.transformationMethod = null
            }
        } else if (type == 1) {
            if (isConfirmPasswordVisible) {
                isConfirmPasswordVisible = false
                binding.ivToggleConfirmPasswordVisibility.setImageResource(R.drawable.ic_eye_hide)
                binding.etConfirmPassword.transformationMethod = PasswordTransformationMethod()
            } else {
                isConfirmPasswordVisible = true
                binding.ivToggleConfirmPasswordVisibility.setImageResource(R.drawable.ic_eye)
                binding.etConfirmPassword.transformationMethod = null
            }

        }

    }

    private fun validateSignupForm() {

        name = binding.etFullName.text.toString().trim()
        email = binding.etEmail.text.toString().trim()
        phoneNumber = binding.etPhone.text.toString().trim()
        phoneCode = "+91"
        password = binding.etPassword.text.toString().trim()
        confirmPassword = binding.etConfirmPassword.text.toString().trim()


        if (name!!.isEmpty()) {
            mIBaseActivity?.showMessage(getString(R.string.messageNameRequired))
            return
        }

        if (email!!.isEmpty()) {
            mIBaseActivity?.showMessage(getString(R.string.messageEmailRequired))
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email!!).matches()) {
            mIBaseActivity?.showMessage(getString(R.string.messageInvalidEmail))
            return
        }


        if (phoneNumber!!.isEmpty()) {
            mIBaseActivity?.showMessage(getString(R.string.messagePhoneRequired))
            return
        }

        if (!Patterns.PHONE.matcher(phoneNumber!!).matches()) {
            mIBaseActivity?.showMessage(getString(R.string.messageInvalidPhone))
            return
        }

        if (password!!.isEmpty()) {
            mIBaseActivity?.showMessage(getString(R.string.messagePasswordRequired))
            return
        }

        if (confirmPassword!!.isEmpty()) {
            mIBaseActivity?.showMessage(getString(R.string.messageConfirmPasswordRequired))
            return
        }

        if (password != confirmPassword) {
            mIBaseActivity?.showMessage(getString(R.string.messagePasswordNotMatched))
            return
        }


        authViewModel.sendOtp(phoneCode!!, phoneNumber!!)


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