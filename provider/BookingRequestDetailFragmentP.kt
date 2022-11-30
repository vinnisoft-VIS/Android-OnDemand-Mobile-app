package com.vrsidekick.fragments.provider

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.vrsidekick.R
import com.vrsidekick.activities.BaseActivity
import com.vrsidekick.adapter.provider.PropertySlidesAdapterP
import com.vrsidekick.databinding.FragmentBookingRequestDetailPBinding
import com.vrsidekick.utils.setSafeOnClickListener

private const val TAG ="BookingRequestDetailFragmentP"
class BookingRequestDetailFragmentP : Fragment() {
  private lateinit var binding : FragmentBookingRequestDetailPBinding
  private var mPropertySlidesAdapter: PropertySlidesAdapterP? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPropertySlidesAdapter = PropertySlidesAdapterP()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentBookingRequestDetailPBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as BaseActivity).isShowBottomNavBar()
        setupPager()
        setupClickListener()
    }



    private fun setupPager() {
       binding.pagerPropertyImages.adapter = mPropertySlidesAdapter
    }

    private fun setupClickListener() {
        binding.ivBack.setSafeOnClickListener(1000) {
            it.findNavController().navigateUp()
        }

    }
}