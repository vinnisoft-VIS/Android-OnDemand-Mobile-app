package com.vrsidekick.fragments.common

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.braintreepayments.cardform.view.CardForm
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.stripe.android.ApiResultCallback
import com.stripe.android.Stripe
import com.stripe.android.model.Card
import com.stripe.android.model.CardParams
import com.stripe.android.model.Token
import com.vrsidekick.R
import com.vrsidekick.activities.BaseActivity
import com.vrsidekick.activities.prefs
import com.vrsidekick.databinding.FragmentAddCardBinding
import com.vrsidekick.interfaces.IBaseActivity
import com.vrsidekick.utils.Constants
import com.vrsidekick.utils.setSafeOnClickListener
import com.vrsidekick.viewModels.AccountViewModel
import com.vrsidekick.viewModels.ManagePaymentViewModel


private const val TAG= "AddCardFragment"
class AddCardFragment : Fragment() {
    private lateinit var binding : FragmentAddCardBinding
    private val managePaymentViewModel : ManagePaymentViewModel by viewModels()
    private var mIBaseActivity : IBaseActivity? =null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddCardBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as BaseActivity).isShowBottomNavBar()
        setupCardForm()
        setupObserver()
        setupClickListener()



    }


    private fun setupCardForm() {


        binding.cardForm.cardRequired(true)
            .expirationRequired(true)
            .cvvRequired(true)
            .cardholderName(CardForm.FIELD_REQUIRED)
            .postalCodeRequired(false)
            .mobileNumberRequired(false)
            .mobileNumberExplanation("SMS is required on this number")
            .actionLabel("Purchase")
            .setup(activity)
    }


    private fun setupObserver() {
        managePaymentViewModel.getProgressObserver.observe(viewLifecycleOwner){
            mIBaseActivity?.showProgressDialog(it)

        }
        managePaymentViewModel.getMessageObserver.observe(viewLifecycleOwner){
           mIBaseActivity?.showMessage(it)
        }

        managePaymentViewModel.getAddCardObserver.observe(viewLifecycleOwner){
            MaterialAlertDialogBuilder(requireContext(),R.style.MaterialAlertDialog_App)
                .setTitle(getString(R.string.success))
                .setMessage(it.message)
                .setPositiveButton(getString(R.string.dismiss)){dialog,which ->
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

        binding.btAdd.setSafeOnClickListener {
            mIBaseActivity?.hideKeyboard()
            validateCard()

        }
    }

    private fun validateCard() {
        mIBaseActivity?.showProgressDialog(true)
        binding.cardForm.validate()
        if(binding.cardForm.isValid){
            val cardNumber = binding.cardForm.cardNumber
            val month = binding.cardForm.expirationMonth
            val year = binding.cardForm.expirationYear
            val cvv = binding.cardForm.cvv

            val cardParam = CardParams(cardNumber,month.toInt(),year.toInt(),cvv)

            val stripe:Stripe = Stripe(requireContext(),Constants.STRIPE_PK)

            stripe.createCardToken(cardParam, callback = object :ApiResultCallback<Token>{
                override fun onError(e: Exception) {
                    MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialog_App)
                        .setCancelable(false)
                        .setTitle(getString(R.string.error))
                        .setMessage(getString(R.string.messageCardNotValid))
                        .setPositiveButton(getString(com.vrsidekick.R.string.dismiss)){ dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()

                }

                override fun onSuccess(result: Token) {
                    val cardToken = result.id
                    managePaymentViewModel.addCard(
                        "Bearer ${prefs.userToken}",
                        Constants.HEADER_X_REQUESTED_WITH,
                        cardToken
                    )
//add to card
                }

            })

        }else{
            mIBaseActivity?.showProgressDialog(false)
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