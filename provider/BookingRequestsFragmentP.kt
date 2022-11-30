package com.vrsidekick.fragments.provider

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.vrsidekick.NavGraphProviderDirections
import com.vrsidekick.R
import com.vrsidekick.adapter.provider.BookingRequestAdapterP
import com.vrsidekick.databinding.FragmentBookingRequestsPBinding

private const val TAG ="BookingRequestsFragmentP"
class BookingRequestsFragmentP : Fragment(), BookingRequestAdapterP.IBookingRequestsAdapterP {
    private lateinit var binding : FragmentBookingRequestsPBinding
    private var mBookingRequestAdapterP : BookingRequestAdapterP? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBookingRequestAdapterP = BookingRequestAdapterP()
        mBookingRequestAdapterP?.setOnBookingRequestsClickListener(this)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBookingRequestsPBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvBookingsRequests.setHasFixedSize(true)
        binding.rvBookingsRequests.adapter = mBookingRequestAdapterP
    }

    override fun onItemClick() {
        val direction = ScheduleFragmentPDirections.actionNavScheduleProviderToBookingRequestDetailFragmentP()
        findNavController().navigate(direction)
    }
}