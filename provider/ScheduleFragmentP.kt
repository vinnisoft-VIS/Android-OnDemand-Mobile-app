package com.vrsidekick.fragments.provider

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayoutMediator
import com.vrsidekick.R
import com.vrsidekick.activities.BaseActivity
import com.vrsidekick.activities.prefs
import com.vrsidekick.adapter.provider.BookingsAdapterP
import com.vrsidekick.adapter.provider.SchedulePagerAdapterP
import com.vrsidekick.databinding.FragmentSchedulePBinding
import com.vrsidekick.interfaces.IBaseActivity
import com.vrsidekick.viewModels.PostsViewModel
import com.vrsidekick.viewModels.PropertyViewModel
import com.vrsidekick.viewModels.ProviderViewModel

private const val TAG ="ScheduleFragmentP"
class ScheduleFragmentP : Fragment(), BookingsAdapterP.IBookingsAdapter {
    private lateinit var binding : FragmentSchedulePBinding
    private val providerViewModel : ProviderViewModel by viewModels()
    private val propertyViewModel : PropertyViewModel by viewModels()
    private var mIBaseActivity : IBaseActivity? =null
    private var mBookingsAdapterP : BookingsAdapterP? =null
    private var mClickedPosition:Int = -1




  //  private var mSchedulePagerAdapter : SchedulePagerAdapterP? =null
   // private val tabs= arrayOf(BookingsFragmentP(),BookingRequestsFragmentP())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBookingsAdapterP = BookingsAdapterP(requireContext())
        mBookingsAdapterP?.setOnBookingClickListener(this)
        mBookingsAdapterP?.registerAdapterDataObserver(object :RecyclerView.AdapterDataObserver(){

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                binding.rvBookings.scrollToPosition(0)
            }
        })



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSchedulePBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as BaseActivity).isShowBottomNavBar(true)
        binding.rvBookings.adapter = mBookingsAdapterP
        setupObserver()
        setupPropertyObserver()
        providerViewModel.getProviderBookings(
            "Bearer ${prefs.userToken}"
        )
       // setupPager()

    }

    private fun setupPropertyObserver() {
        propertyViewModel.getProgressObserver.observe(viewLifecycleOwner){
          mIBaseActivity?.showProgressDialog(it)
        }

        propertyViewModel.getMessageObserver.observe(viewLifecycleOwner){
           mIBaseActivity?.showMessage(it)
        }

        propertyViewModel.getCancelBookingObserver.observe(viewLifecycleOwner){
            mBookingsAdapterP?.removeItem(mClickedPosition)

        }
    }

    /* private fun setupPager() {
         mSchedulePagerAdapter = SchedulePagerAdapterP(tabs,this)
         binding.pagerSchedule.adapter = mSchedulePagerAdapter
         TabLayoutMediator(binding.tabSchedules,binding.pagerSchedule){tab,position ->
             when(position){
                 0 -> tab.text = "Booking"
                 1 -> tab.text = "Requests"
             }

         }.attach()
     }*/



    private fun setupObserver() {

        providerViewModel.getMessageObserver.observe(viewLifecycleOwner) {
            mIBaseActivity?.showMessage(it)
        }

        providerViewModel.getProgressObserver.observe(viewLifecycleOwner) {
            mIBaseActivity?.showProgressDialog(it)
        }


        providerViewModel.getProviderBookingsObserver.observe(viewLifecycleOwner) {
            if(it.isEmpty()){
                binding.rvBookings.visibility = View.GONE
                binding.layoutNoData.root.visibility = View.VISIBLE
            }else{
                binding.rvBookings.visibility = View.VISIBLE
                binding.layoutNoData.root.visibility = View.GONE
                mBookingsAdapterP?.submitList(it)
            }


        }




    }

    override fun onStop() {
        super.onStop()
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

    override fun onPropertyClick(propertyId: Long) {
        val direction = ScheduleFragmentPDirections.actionNavScheduleProviderToBookingDetailFragmentP(propertyId)
        findNavController().navigate(direction)
    }

    override fun onCancelBooking(position:Int,bookingId: Long) {
        mClickedPosition = position
        showCancelBookingAlert(bookingId)

    }

    private fun showCancelBookingAlert(bookingId: Long) {
        MaterialAlertDialogBuilder(requireContext(),R.style.MaterialAlertDialog_App)
            .setTitle(getString(R.string.alert))
            .setMessage(getString(R.string.messageCancelBooking))
            .setPositiveButton(getString(R.string.dismiss)){dialog,which ->
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.cancelBooking)){dialog,which ->
                dialog.dismiss()

                propertyViewModel.cancelBooking(
                    "Bearer ${prefs.userToken}",
                    bookingId
                )
            }.show()

    }
}