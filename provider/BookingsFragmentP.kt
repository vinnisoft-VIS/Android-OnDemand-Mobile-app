package com.vrsidekick.fragments.provider

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.vrsidekick.R
import com.vrsidekick.adapter.provider.BookingsAdapterP
import com.vrsidekick.databinding.FragmentBookingsPBinding

private const val TAG= "BookingsFragmentP"
class BookingsFragmentP : Fragment() {
    private lateinit var binding : FragmentBookingsPBinding
    private var mBookingsAdapter : BookingsAdapterP? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBookingsAdapter = BookingsAdapterP(requireContext())
           // mBookingsAdapter?.setOnBookingClickListener(this)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBookingsPBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvBookings.setHasFixedSize(true)
        binding.rvBookings.adapter = mBookingsAdapter
    }


}