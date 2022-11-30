package com.vrsidekick.fragments.provider


import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.model.LatLng
import com.vrsidekick.NavGraphProviderDirections
import com.vrsidekick.R
import com.vrsidekick.activities.BaseActivity
import com.vrsidekick.activities.ProviderActivity
import com.vrsidekick.activities.prefs
import com.vrsidekick.adapter.user.ServiceCategoryHomeAdapter
import com.vrsidekick.adapter.provider.PropertyAdsAdapterP
import com.vrsidekick.databinding.FragmentHomePBinding
import com.vrsidekick.interfaces.IBaseActivity
import com.vrsidekick.models.Category
import com.vrsidekick.utils.LocationHelper
import com.vrsidekick.utils.setAllOnClickListener
import com.vrsidekick.viewModels.PostsViewModel

private const val TAG ="HomeFragment"
class HomeFragmentP : Fragment(),
    PropertyAdsAdapterP.IPropertyAdsAdapterP, ServiceCategoryHomeAdapter.IServiceCategoryHome,
    LocationHelper.ILocationHelper {
    private lateinit var binding : FragmentHomePBinding
    private var mServiceCateogryAdapter : ServiceCategoryHomeAdapter? =null
    private var mPropertyAdsAdapter : PropertyAdsAdapterP? =null
    private val postsViewModel: PostsViewModel by viewModels()
    private var mIBaseActivity: IBaseActivity? = null





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as BaseActivity).getLocationHelper().setOnLocationUpdateListener(this)


        mServiceCateogryAdapter = ServiceCategoryHomeAdapter(requireContext())
        mServiceCateogryAdapter?.setOnServiceCategoryClickListener(this)

        mPropertyAdsAdapter = PropertyAdsAdapterP()
        mPropertyAdsAdapter?.setOnPropertyAdsClickListener(this)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomePBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: ")

        (activity as BaseActivity).isShowBottomNavBar(true)
        (activity as BaseActivity).changeLightStatusBar(R.color.color_background)
        (activity as BaseActivity).getLocationHelper().getCurrentLocation()
        binding.rvServiceCategories.adapter = mServiceCateogryAdapter
        binding.rvPropertyAds.adapter = mPropertyAdsAdapter

        setupClickListener()
        setupObserver()
        postsViewModel.getServiceCategories(
            "Bearer ${prefs.userToken}"
        )
    }

    private fun setupObserver() {

        postsViewModel.getMessageObserver.observe(viewLifecycleOwner) {
            mIBaseActivity?.showMessage(it)

        }

        postsViewModel.getProgressObserver.observe(viewLifecycleOwner) {
            mIBaseActivity?.showProgressDialog(it)
        }

        postsViewModel.getServiceCategoriesObserver.observe(viewLifecycleOwner) {
            if(it.isNotEmpty()){
                it.first().isSelected = true
                mServiceCateogryAdapter?.submitList(it)
                postsViewModel.getPropertyAds(
                    "Bearer ${prefs.userToken}",
                    it.first().id
                )

            }


        }
        postsViewModel.getPropertyAdsObserver.observe(viewLifecycleOwner) {
            if(it.isEmpty()){
                binding.rvPropertyAds.visibility = View.GONE
                binding.layoutNoData.root.visibility = View.VISIBLE
            }else{
                binding.rvPropertyAds.visibility = View.VISIBLE
                binding.layoutNoData.root.visibility = View.GONE
                mPropertyAdsAdapter?.submitList(it)
            }


        }




    }

    private fun setupClickListener() {
        binding.gpLocation.setAllOnClickListener{
           /* val direction = HomeFragmentPDirections.actionHomeFragmentPToSelectLocationFragment()
            findNavController().navigate(direction)*/

            (activity as BaseActivity).getLocationHelper().searchPlaces()
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

    override fun onItemClick(propertyId:Long) {
        val direction = NavGraphProviderDirections.actionGlobalPropertyDetailFragmentP(propertyId)
        findNavController().navigate(direction)
    }



    override fun onLocationResult(latLng: LatLng) {
        Log.d(TAG, "onLocationResult: ${(activity as BaseActivity).getLocationHelper().getAddress(requireContext(),latLng)}")
        val locationName = (activity as BaseActivity).getLocationHelper().getAddress(requireContext(),latLng)
        binding.tvAddress.text = locationName
    }

    override fun onCategorySelect(category: Category) {
        postsViewModel.getPropertyAds(
            "Bearer ${prefs.userToken}",
            category.id
        )
    }


}