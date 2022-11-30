package com.vrsidekick.fragments.user

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.vrsidekick.NavGraphDirections
import com.vrsidekick.R
import com.vrsidekick.activities.BaseActivity
import com.vrsidekick.activities.prefs
import com.vrsidekick.adapter.user.ServiceCategoryFilterAdapter
import com.vrsidekick.databinding.FragmentFilterBinding
import com.vrsidekick.fragments.provider.ServiceTimeType
import com.vrsidekick.interfaces.IBaseActivity
import com.vrsidekick.models.Category
import com.vrsidekick.utils.setSafeOnClickListener
import com.vrsidekick.viewModels.PostsViewModel
import com.vrsidekick.viewModels.ProviderViewModel
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*


private const val TAG = "FilterFragment"

enum class FilterTimeType {
    START, END
}

class FilterFragment : Fragment(), ServiceCategoryFilterAdapter.IServiceCatFilter {
    private lateinit var binding: FragmentFilterBinding
    private var mServiceCategoryFilterAdapter: ServiceCategoryFilterAdapter? = null
    private val postsViewModel: PostsViewModel by viewModels()
    private val providerViewModel: ProviderViewModel by viewModels()
    private var mIBaseActivity: IBaseActivity? = null
    private var mSelectedCategoryId: Long? = null
    private var mFilterDistance: Int? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mServiceCategoryFilterAdapter = ServiceCategoryFilterAdapter(requireContext())
        mServiceCategoryFilterAdapter?.setServiceCatFilterListener(this)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as BaseActivity).isShowBottomNavBar()
        binding.rvServiceCategories.setHasFixedSize(true)
        setupDistanceSlider()

        // setupPriceRangeSlider()

        setupPostsObserver()
        setupProviderObserver()

        setupClickListener()
        postsViewModel.getServiceCategories(
            "Bearer ${prefs.userToken}",
        )
    }

    private fun setupProviderObserver() {
        providerViewModel.getProgressObserver.observe(viewLifecycleOwner) {
            mIBaseActivity?.showProgressDialog(it)
        }
        providerViewModel.getMessageObserver.observe(viewLifecycleOwner) {
            mIBaseActivity?.showMessage(it)
        }
        providerViewModel.getFilterProviderObserver.observe(viewLifecycleOwner) {
            val direction = NavGraphDirections.actionGlobalProviderAdsListFragment(it)
            findNavController().navigate(direction)

        }
    }

    private fun setupPostsObserver() {
        postsViewModel.getProgressObserver.observe(viewLifecycleOwner) {
            mIBaseActivity?.showProgressDialog(it)
        }
        postsViewModel.getMessageObserver.observe(viewLifecycleOwner) {
            mIBaseActivity?.showMessage(it)
        }
        postsViewModel.getServiceCategoriesObserver.observe(viewLifecycleOwner) {
            Log.d(TAG, "setupPostsObserver: size = ${it.size}")
            it.first().isSelected = true
            mSelectedCategoryId = it.first().id
            mServiceCategoryFilterAdapter?.submitList(it)
            binding.rvServiceCategories.adapter = mServiceCategoryFilterAdapter

        }

    }


    private fun setupClickListener() {
        binding.ivBack.setSafeOnClickListener(1000) {
            findNavController().navigateUp()
        }

        binding.btStartHour.setSafeOnClickListener(1000) {
            selectTime(FilterTimeType.START)
        }

        binding.btEndHour.setSafeOnClickListener(1000) {
            selectTime(FilterTimeType.END)
        }

        binding.btApplyFilters.setSafeOnClickListener(1000) {
            validateFilterFormData()
        }

        binding.btReset.setOnClickListener {
            resetData()
        }
    }

    private fun validateFilterFormData() {
        val location = prefs.currentLocation
        val serviceType = getServiceType()

        val requireTools =getRequireTools()

        val requireSupplies = getRequireSupplies()
        val rating = binding.ratingBar.rating.toString()
        val startHours = binding.btStartHour.text.toString().trim()
        val endHours = binding.btEndHour.text.toString().trim()
        val priceRangeStart = if(binding.sliderPriceRange.values[0] == 0f) "" else binding.sliderPriceRange.values[0].toString()
        val priceRangeEnd = if(binding.sliderPriceRange.values[1] == 0f) "" else binding.sliderPriceRange.values[1].toString()
        mFilterDistance = binding.sliderDistanceRange.value.toInt()


        Log.d(
            TAG, "validateFilterFormData:" +
                    " token = Bearer ${prefs.userToken} \n" +
                    " location = $location \n" +
                    " serviceType = $serviceType \n" +
                    " requireTools = $requireTools \n" +
                    " requireSupplies = $requireSupplies \n" +
                    " rating = $rating \n" +
                    " startHours = $startHours \n" +
                    " endHours = $endHours \n" +
                    " priceRangeStart = $priceRangeStart \n" +
                    " priceRangeEnd = $priceRangeEnd \n" +
                    " distanceInMiles = $mFilterDistance \n" +
                    " serviceCatId = $mSelectedCategoryId \n"
        )

        try {
            providerViewModel.filterProvider(
                "Bearer ${prefs.userToken}",
                mSelectedCategoryId!!,
                startHours, endHours,
                mFilterDistance!!,
                location!!.latitude,
                location.longitude,
                requireTools,
                requireSupplies,
                serviceType, priceRangeStart, priceRangeEnd,
                rating
            )
        }catch (e:Exception){

        }
    }

    private fun getServiceType(): String {

       return when(binding.rgServiceType.checkedRadioButtonId){
                binding.rbHourly.id ->"0"
                binding.rbMonthly.id ->"1"
           else -> ""
       }

    }

    private fun getRequireTools(): String {
        return when(binding.rgToolsRequired.checkedRadioButtonId){
            binding.rbToolsRequiredNo.id -> "0"
            binding.rbToolsRequiredNo.id -> "1"
           else -> ""
        }
    }


    private fun getRequireSupplies(): String {
        return when(binding.rgSuppliesRequired.checkedRadioButtonId){
            binding.rbSuppliesRequiredNo.id -> "0"
            binding.rbSuppliesRequiredYes.id -> "1"
            else -> ""
        }
    }

    private fun setupDistanceSlider() {
        setFilterDistance(binding.sliderDistanceRange.value.toInt())
        binding.sliderDistanceRange.addOnChangeListener { slider, value, fromUser ->
            if (fromUser) {
                setFilterDistance(value.toInt())
            }
        }
    }


    private fun setFilterDistance(value: Int) {
        mFilterDistance = value
        binding.tvStartMiles.text = String.format("%s miles", value.toString())
    }




    private fun selectTime(type: FilterTimeType) {
        val calendar = Calendar.getInstance()
        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(calendar.get(Calendar.HOUR))
            .setMinute(calendar.get(Calendar.MINUTE))
            .setTitleText(getString(R.string.selectServiceTime))
            .build()

        timePicker.show(childFragmentManager, TAG)
        timePicker.addOnPositiveButtonClickListener {

            val hour = if (timePicker.hour < 10) "0${timePicker.hour}" else timePicker.hour
            val minutes = if (timePicker.minute < 10) "0${timePicker.minute}" else timePicker.minute

            val tempTime = "$hour:$minutes"
            val sdf1 = SimpleDateFormat("HH:mm", Locale.getDefault()).parse(tempTime)
            val result = SimpleDateFormat("hh:mm aa", Locale.getDefault()).format(sdf1)

            if (type == FilterTimeType.START) {
                binding.btStartHour.text = result
            } else {
                binding.btEndHour.text = result
            }
        }
        timePicker.addOnNegativeButtonClickListener {

        }
        timePicker.addOnCancelListener {
            // call back code
        }
        timePicker.addOnDismissListener {
            // call back code
        }

    }


    private fun resetData(){
        binding.rgSuppliesRequired.clearCheck()
        binding.rgToolsRequired.clearCheck()
        binding.rgServiceType.clearCheck()
        binding.ratingBar.rating = 1f
        binding.btStartHour.text =""
        binding.btEndHour.text =""
        binding.sliderPriceRange.values = listOf<Float>(10.0f,5000.0f)
        binding.sliderDistanceRange.value = 4f
        mServiceCategoryFilterAdapter?.notifyItemSelected(0)
       mSelectedCategoryId = mServiceCategoryFilterAdapter?.currentList?.get(0)?.id

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

    override fun onCategoryClick(categoryId: Long) {
        mSelectedCategoryId = categoryId
    }
}