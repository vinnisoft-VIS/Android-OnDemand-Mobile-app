package com.vrsidekick.fragments.provider

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.vrsidekick.R
import com.vrsidekick.activities.BaseActivity
import com.vrsidekick.activities.prefs
import com.vrsidekick.adapter.provider.SelectedWeekDaysAdapter
import com.vrsidekick.adapter.provider.ServiceCatArrayAdapter
import com.vrsidekick.adapter.provider.WeekDaysAdapterP
import com.vrsidekick.databinding.FragmentAddServicePBinding
import com.vrsidekick.dialogFragments.ChooseTimeDialogFragment
import com.vrsidekick.interfaces.IBaseActivity
import com.vrsidekick.models.Category
import com.vrsidekick.models.SelectedWeekDaysModel
import com.vrsidekick.utils.setAllOnClickListener
import com.vrsidekick.utils.setSafeOnClickListener
import com.vrsidekick.viewModels.PostsViewModel
import com.vrsidekick.viewModels.ProviderViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

private const val TAG = "AddServiceFragmentP"

enum class ServiceTimeType {
    START, END
}

class AddServiceFragmentP : Fragment(), WeekDaysAdapterP.IWeekDaysAdapterP,
    ChooseTimeDialogFragment.IChooseTime {
    private lateinit var binding: FragmentAddServicePBinding
    private val postsViewModel: PostsViewModel by viewModels()
    private val providerViewModel : ProviderViewModel by viewModels()
    private var mIBaseActivity: IBaseActivity? = null
    private var mWeekDaysAdapter: WeekDaysAdapterP? = null
    private var mSelectedWeekDaysAdapter : SelectedWeekDaysAdapter? =null
    private var mServiceCategoryAdapter: ServiceCatArrayAdapter? = null
    private var mServiceWithinMiles: Int = 4
    private var mTimeStartService: String? = null
    private var mTimeEndService: String? = null
    private var mClickedDayValue = 0;
    private var mSelectedServiceCat: Category? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mWeekDaysAdapter = WeekDaysAdapterP(requireContext())
        mSelectedWeekDaysAdapter = SelectedWeekDaysAdapter(requireContext())
        mWeekDaysAdapter?.setOnWeekDaysClickListener(this)
        mServiceCategoryAdapter =
            ServiceCatArrayAdapter(requireContext(), android.R.layout.simple_list_item_1)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddServicePBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupLocation()
        (activity as BaseActivity).isShowBottomNavBar()
        setServiceDistance(mServiceWithinMiles)
        binding.rvWeekDays.setHasFixedSize(true)
        binding.rvSelectedDays.setHasFixedSize(true)
        binding.rvWeekDays.adapter = mWeekDaysAdapter
        binding.rvSelectedDays.adapter = mSelectedWeekDaysAdapter
        setupSlider()
        setupClickListener()
        setupPostObserver()
        setupProviderServiceObserver()
        getServiceCategories()

    }

    private fun setupLocation() {

        binding.tvAddress.text =
            (activity as BaseActivity).getLocationHelper().getAddress(requireContext(), prefs.currentLocation)
    }


    private fun getServiceCategories() {
        postsViewModel.getServiceCategories("Bearer ${prefs.userToken}")
    }



    private fun setupProviderServiceObserver() {
        providerViewModel.getProgressObserver.observe(viewLifecycleOwner) {
            mIBaseActivity?.showProgressDialog(it)
        }

        providerViewModel.getMessageObserver.observe(viewLifecycleOwner) {
            mIBaseActivity?.showMessage(it)
        }

        providerViewModel.getAddServiceObserver.observe(viewLifecycleOwner){
            MaterialAlertDialogBuilder(requireContext(),R.style.MaterialAlertDialog_App)
                .setTitle(getString(R.string.success))
                .setMessage(it.message)
                .setPositiveButton(getString(R.string.dismiss)){dialog,which ->
                    dialog.dismiss()
                    findNavController().navigateUp()
                }.show()
        }

    }




    private fun setupPostObserver() {
        postsViewModel.getProgressObserver.observe(viewLifecycleOwner) {
            mIBaseActivity?.showProgressDialog(it)
        }

        postsViewModel.getMessageObserver.observe(viewLifecycleOwner) {
            mIBaseActivity?.showMessage(it)
        }

        postsViewModel.getServiceCategoriesObserver.observe(viewLifecycleOwner) {
            mServiceCategoryAdapter?.setData(it)
            binding.autocompleteEtService.setAdapter(mServiceCategoryAdapter)

            binding.autocompleteEtService.setOnItemClickListener { adapterView, view, i, l ->
                mIBaseActivity?.hideKeyboard()
                val category = mServiceCategoryAdapter?.getItem(i)
                mSelectedServiceCat = category
                category?.let {
                    binding.autocompleteEtService.setText(it.name)
                }
            }

        }
    }

    private fun setupSlider() {
        binding.sliderDistanceRange.addOnChangeListener { slider, value, fromUser ->
            if (fromUser) {
                setServiceDistance(value.toInt())
            }
        }
    }

    private fun setServiceDistance(value: Int) {
        mServiceWithinMiles = value
        binding.tvStartMiles.text = String.format("%s miles", value.toString())
    }

    private fun setupClickListener() {
        binding.ivClose.setSafeOnClickListener(1000) {
            it.findNavController().navigateUp()
        }


        binding.gpLocation.setAllOnClickListener {
            /*  val direction = HomeFragmentDirections.actionNavHomeToSelectLocationFragment()
              findNavController().navigate(direction)*/
            (activity as BaseActivity).getLocationHelper().searchPlaces()
        }


        binding.btPost.setSafeOnClickListener(1000) {
            validateAddServiceForm()
        }
    }

    private fun validateAddServiceForm() {
        val location = prefs.currentLocation
        val skills = binding.etSkillExperience.text.toString().trim()
        val monthlyRate = binding.etMonthlyRate.text.toString().trim()
        val hourlyRate = binding.etHourlyRate.text.toString().trim()
        val provideOwnTools = getProvideOwnTools()
        val provideOwnSupplies = getProvideOwnSupplies()

       val selectedWeekDay = mSelectedWeekDaysAdapter?.getData()?: ArrayList()
      //  val timeTill = binding.tvEndTime.text.toString().trim()

        if (location == null) {
            mIBaseActivity?.showMessage(getString(R.string.messageSelectLocation))
            return
        }

        if (location.latitude == 0.0 || location.longitude == 0.0) {
            mIBaseActivity?.showMessage(getString(R.string.messageSelectLocation))
        }


        if (skills.isEmpty()) {
            mIBaseActivity?.showMessage(getString(R.string.messageEnterYourSkills))
            return
        }

        if (mSelectedServiceCat == null) {
            mIBaseActivity?.showMessage(getString(R.string.messageSelectCategory))
            return
        }

        if (monthlyRate.isEmpty()) {
            mIBaseActivity?.showMessage(getString(R.string.messageEnterMonthlyRates))
            return
        }

        if (hourlyRate.isEmpty()) {
            mIBaseActivity?.showMessage(getString(R.string.messageEnterHourlyRates))
            return
        }

        if (monthlyRate.isEmpty()) {
            mIBaseActivity?.showMessage(getString(R.string.messageEnterMonthlyRates))
            return
        }
        if (selectedWeekDay.isEmpty()) {
            mIBaseActivity?.showMessage(getString(R.string.messageSelectWorkingDays))
            return

        }

        val selectedDaysTriple = getSelectedWeekDay(selectedWeekDay)


        Log.d(
            TAG,
            "validateAddServiceForm: $location" +
                    "serviceInMiles = $mServiceWithinMiles\n" +
                    "skill = $skills\n" +
                    "monthlyRates = $monthlyRate\n" +
                    "hourlyRates = $hourlyRate\n" +
                    "provideOwnTools = $provideOwnTools\n"+
                    "provideOwnSupplies = $provideOwnSupplies\n"+
                    "timeFrom = ${selectedDaysTriple.first}\n"+
                    "timeTill = ${selectedDaysTriple.second}\n"+
                    "days = ${selectedDaysTriple.third}\n"
        )

        providerViewModel.addProviderService(
            "Bearer ${prefs.userToken}",
            location.latitude,
            location.longitude,
            mServiceWithinMiles,
            skills,
            mSelectedServiceCat!!.id,
            monthlyRate,
            hourlyRate,
            provideOwnTools,
            provideOwnSupplies,
            selectedDaysTriple.first,
            selectedDaysTriple.second,
            selectedDaysTriple.third
        )


    }


    private fun getProvideOwnTools(): Int {
        val checkedId = binding.rgProvidesOwnTools.checkedRadioButtonId

        return if (checkedId == binding.rbProvidesOwnToolsYes.id) 1 else 0


    }

    private fun getProvideOwnSupplies(): Int {
        val checkedId = binding.rgProvidesOwnSupplies.checkedRadioButtonId
        return if (checkedId == binding.rbProvidesOwnSuppliesYes.id) 1 else 0

    }

    private fun getSelectedWeekDay(selectedWeekDaysModel: ArrayList<SelectedWeekDaysModel>)
    : Triple<ArrayList<String> ,ArrayList<String> ,ArrayList<Int> > {

        val startTime = ArrayList<String>()
        val endTime = ArrayList<String>()
        val days = ArrayList<Int>()

        selectedWeekDaysModel.forEach {
            startTime.add(it.startTime)
            endTime.add(it.endTime)
            days.add(it.dayValue)
        }
        return Triple(startTime, endTime,days)

    }






    override fun onAttach(context: Context) {
        super.onAttach(context)
        mIBaseActivity = context as IBaseActivity
    }

    override fun onDetach() {
        super.onDetach()
        mIBaseActivity = null
    }

    override fun onDayClick(dayValue: Int,isSelected:Boolean) {
        mClickedDayValue=  dayValue
        if(isSelected){
           mSelectedWeekDaysAdapter?.removeDayAtDayValue(dayValue)
            mWeekDaysAdapter?.selectDay(dayValue)
        }else{
            showChooseTimeDialog()
        }


    }

    private fun showChooseTimeDialog() {
        val chooseTimeDialogFragment = ChooseTimeDialogFragment.newInstance()
        chooseTimeDialogFragment.setChooseTimeClickListener(this)
        chooseTimeDialogFragment.show(childFragmentManager,TAG)
    }

    override fun onSelectTime(startTime: String, endTime: String) {
        mSelectedWeekDaysAdapter?.addDay(SelectedWeekDaysModel(startTime,endTime,mClickedDayValue))
        mWeekDaysAdapter?.selectDay(mClickedDayValue)

    }
}