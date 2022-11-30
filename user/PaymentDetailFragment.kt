package com.vrsidekick.fragments.user

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.vrsidekick.R
import com.vrsidekick.activities.prefs
import com.vrsidekick.databinding.FragmentPaymentBinding
import com.vrsidekick.databinding.FragmentPaymentDetailBinding
import com.vrsidekick.dialogs.SubmitReviewsDialog
import com.vrsidekick.interfaces.IBaseActivity
import com.vrsidekick.models.SidekickDetail
import com.vrsidekick.utils.Global
import com.vrsidekick.utils.loadFromUrl
import com.vrsidekick.viewModels.PropertyViewModel

private const val TAG = "PaymentDetailFragment"
class PaymentDetailFragment : Fragment(),PaymentFragment.IClick {

private lateinit var binding : FragmentPaymentDetailBinding
    private val propertyViewModel: PropertyViewModel by viewModels()
private var  mPaymentFragment : PaymentFragment?=null
    private var mIBaseActivity: IBaseActivity? = null
    private val navArgs : PaymentDetailFragmentArgs by navArgs()
    private var mSidekickDetail: SidekickDetail? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPaymentFragment = PaymentFragment()
        mPaymentFragment?.setOnClickListener(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentPaymentDetailBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getSidekickDetail()
        setupObserver()
        setupListener()
    }

    private fun setupObserver() {
        propertyViewModel.getProgressObserver.observe(viewLifecycleOwner) {
            if (mSidekickDetail != null) {
                mIBaseActivity?.showProgressDialog(false)
            } else {
                mIBaseActivity?.showProgressDialog(it)
            }


        }

        propertyViewModel.getMessageObserver.observe(viewLifecycleOwner) {
            mIBaseActivity?.showMessage(it)
        }

        propertyViewModel.getSidekickDetailObserver.observe(viewLifecycleOwner) {
            setupUi(it)
        }

    }

    private fun setupUi(detailData: SidekickDetail?) {
        mSidekickDetail = detailData
        detailData?.let { detail ->
            binding.ivProperty.loadFromUrl(detail.propertyImage)
            binding.tvPropertyName.text = detail.propertyName
            binding.ivProvider.loadFromUrl(detail.providerImage)
            binding.tvProviderName.text = detail.providerName
            if (detail.status == 1) {
                  binding.tvWorkStatus.text = getString(R.string.completed)
                  binding.tvWorkStatus.setTextColor(
                      ContextCompat.getColor(
                          requireContext(),
                          R.color.color_green_book
                      )
                  )
              }


            binding.tvService.text = detail.categoryName
            Global.formatDate(detail.createdAt)?.let {
                binding.tvDate.text = it
            }
            binding.tvBookingTime.text = detail.time
            binding.tvScheduleType.text = detail.rateType
            binding.tvPrice.text = detail.rate
            binding.tvTimeDuration.text = detail.timeDuration

        }

    }

    private fun getSidekickDetail() {
        propertyViewModel.getSidekickDetail(
            "Bearer ${prefs.userToken}",
            navArgs.id
        )
    }

    private fun setupListener() {
        binding.btPay.setOnClickListener {
            val direction = PaymentDetailFragmentDirections.actionPaymentDetailFragmentToPaymentFragment()
            findNavController().navigate(direction)
        }
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onClick() {
        val dialog = SubmitReviewsDialog()
        dialog.show(childFragmentManager,TAG)
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