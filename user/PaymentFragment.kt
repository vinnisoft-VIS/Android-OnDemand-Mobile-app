package com.vrsidekick.fragments.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.vrsidekick.adapter.common.CardListAdapter
import com.vrsidekick.databinding.FragmentPaymentBinding
import com.vrsidekick.dialogs.SubmitReviewsDialog
private const val TAG = "PaymentFragment"
class PaymentFragment : Fragment() {
   private lateinit var binding : FragmentPaymentBinding
   private var mPaymentCardAdapter : CardListAdapter?=null
    private var mCallback : PaymentFragment.IClick?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         mPaymentCardAdapter = CardListAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPaymentBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
      setupListener()
        binding.rvCards.adapter = mPaymentCardAdapter
    }

    private fun setupListener() {
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.btPay.setOnClickListener {

        //    findNavController().navigateUp()
                //  mCallback?.onClick()
            val dialog = SubmitReviewsDialog()
            dialog.show(childFragmentManager,TAG)

        }
    }
    fun setOnClickListener(callback : IClick){
       mCallback = callback
    }
    interface IClick{
        fun onClick()
    }
}