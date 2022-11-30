package com.vrsidekick.fragments.provider

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.tabs.TabLayoutMediator
import com.vrsidekick.R
import com.vrsidekick.activities.BaseActivity
import com.vrsidekick.activities.prefs
import com.vrsidekick.adapter.provider.PropertySlidesAdapterP
import com.vrsidekick.databinding.FragmentPropertyDetailPBinding
import com.vrsidekick.interfaces.IBaseActivity
import com.vrsidekick.models.Property
import com.vrsidekick.models.PropertyDetail
import com.vrsidekick.utils.setSafeOnClickListener
import com.vrsidekick.viewModels.PostsViewModel
import com.vrsidekick.viewModels.PropertyViewModel

private const val TAG = "PropertyDetailFragmentP"

class PropertyDetailFragmentP : Fragment() {
    private lateinit var binding: FragmentPropertyDetailPBinding
    private var mPropertySlidesAdapter: PropertySlidesAdapterP? = null
    private val propertyViewModel: PropertyViewModel by viewModels()
    private var mIBaseActivity: IBaseActivity? = null
    private val navArgs: PropertyDetailFragmentPArgs by navArgs()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPropertySlidesAdapter = PropertySlidesAdapterP()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPropertyDetailPBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as BaseActivity).isShowBottomNavBar()
        setupViewPager()
        setupClickListener()
        setupObserver()
        propertyViewModel.getPropertyDetail(
            "Bearer ${prefs.userToken}",
            navArgs.propertyId
        )

    }

    private fun setupObserver() {
        propertyViewModel.getMessageObserver.observe(viewLifecycleOwner) {
            mIBaseActivity?.showMessage(it)
        }

        propertyViewModel.getProgressObserver.observe(viewLifecycleOwner) {
            mIBaseActivity?.showProgressDialog(it)
        }

        propertyViewModel.getPropertyDetailObserver.observe(viewLifecycleOwner) {
            it?.let { detail ->
                setupUiData(detail)
            }


        }
    }

    private fun setupUiData(it: Property) {
        mPropertySlidesAdapter?.setData(it.images)
        binding.tvPropertyName.text = it.name ?: ""
        try {
            binding.tvAddress.text = (activity as BaseActivity).getLocationHelper().getAddress(
                requireContext(),
                LatLng(it.latitude?.toDouble()?: 0.0, it.longitude?.toDouble() ?: 0.0)
            )
        }catch (e:Exception){
            binding.tvAddress.text = getString(R.string.noAddressAvailable)
        }

        binding.tvDescription.text = it.description ?: ""
        binding.tvServiceName.text = it.categoryName ?: ""
        binding.tvLotSize.text = it.lotSize ?: ""
        binding.tvHomeArea.text = it.homeSqFt ?: ""
        binding.tvFloors.text = it.floors ?: ""
        binding.tvBedrooms.text = it.bedrooms ?: ""
        binding.tvBeds.text = it.beds ?: ""
        binding.tvSleepsNumber.text = it.sleepsNumber ?: ""
        binding.tvServiceType.text = it.option.toString()

    }

    private fun setupViewPager() {
        binding.pagerPropertyImages.adapter = mPropertySlidesAdapter
        TabLayoutMediator(
            binding.tabIndicator,
            binding.pagerPropertyImages
        ) { tab, index -> }.attach()
    }

    private fun setupClickListener() {
        binding.ivBack.setSafeOnClickListener(1000) {
            it.findNavController().navigateUp()
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