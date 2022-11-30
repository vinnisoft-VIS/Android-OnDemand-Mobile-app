package com.vrsidekick.fragments.user

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.vrsidekick.R
import com.vrsidekick.activities.prefs
import com.vrsidekick.databinding.FragmentBookingConfirmationBinding
import com.vrsidekick.interfaces.IBaseActivity
import com.vrsidekick.utils.PricingType
import com.vrsidekick.utils.loadFromUrl
import com.vrsidekick.utils.setSafeOnClickListener
import com.vrsidekick.viewModels.ProviderViewModel

private const val TAG = "BookingConfirmationFragment"

class BookingConfirmationFragment : Fragment() {
    private lateinit var binding: FragmentBookingConfirmationBinding
    private val providerViewModel: ProviderViewModel by viewModels()
    private val navArgs: BookingConfirmationFragmentArgs by navArgs()
    private var mIBaseActivity: IBaseActivity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBookingConfirmationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUiData()

        setupClickListener()
        setupObserver()
    }

    private fun setupUiData() {
        val providerDetail = navArgs.providerDetail
        binding.ivProvider.loadFromUrl(providerDetail.profilePhotoUrl)
        binding.tvProviderName.text = providerDetail.name
        binding.tvRating.text = providerDetail.ratingAvg
        binding.tvReview.text = "${providerDetail.ratingCount} Reviews"
        binding.tvPrice.text = String.format(
            "$%s/hr,$%s/mth",
            providerDetail.hourlyRate ?: "0",
            providerDetail.monthlyRate ?: "0"
        )

        binding.tvDate.text = navArgs.date
        binding.tvTime.text = navArgs.time

        if(navArgs.pricingType == PricingType.HOURLY){
            binding.tvRateHeader.text = getString(R.string.hourlyRate)
            binding.tvRate.text = String.format("$%s/hr",providerDetail.hourlyRate?:0)
        }else{
            binding.tvRateHeader.text = getString(R.string.monthlyRate)
            binding.tvRate.text = String.format("$%s/mth",providerDetail.monthlyRate?:0)
        }

    }

    private fun setupObserver() {
        providerViewModel.getProgressObserver.observe(viewLifecycleOwner) {
            mIBaseActivity?.showProgressDialog(it)
        }
        providerViewModel.getMessageObserver.observe(viewLifecycleOwner) {
            mIBaseActivity?.showMessage(it)

        }
        providerViewModel.getBookProviderObserver.observe(viewLifecycleOwner) {
            MaterialAlertDialogBuilder(requireContext(),R.style.MaterialAlertDialog_App)
                .setTitle(getString(R.string.success))
                .setMessage(it.message)
                .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                    dialog.dismiss()
                    findNavController().popBackStack(R.id.nav_home_user,false)
                }.show()

        }
    }

    private fun setupClickListener() {
        binding.ivBack.setSafeOnClickListener {
            it.findNavController().navigateUp()
        }

        binding.btBook.setSafeOnClickListener {
            providerViewModel.bookProvider(
                "Bearer ${prefs.userToken}",
                navArgs.providerDetail.id?:return@setSafeOnClickListener,
                navArgs.propertyId,
                navArgs.date,
                navArgs.time,
                navArgs.pricingType.name,
                getProviderRate(),
                "$4.0",
                navArgs.cateogoryId
            )

        }
    }

    private fun getProviderRate(): String {

        return  if(navArgs.pricingType == PricingType.MONTHLY)
        "$${navArgs.providerDetail.monthlyRate.toString()}"
        else "$${navArgs.providerDetail.hourlyRate.toString()}"


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