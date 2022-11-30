package com.vrsidekick.fragments.user

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.model.LatLng
import com.vrsidekick.activities.UserActivity
import com.vrsidekick.NavGraphDirections
import com.vrsidekick.R
import com.vrsidekick.activities.BaseActivity
import com.vrsidekick.activities.prefs
import com.vrsidekick.adapter.user.ServiceCategoryHomeAdapter
import com.vrsidekick.adapter.user.ServiceProviderAdapter
import com.vrsidekick.databinding.FragmentHomeBinding
import com.vrsidekick.interfaces.IBaseActivity
import com.vrsidekick.models.Category
import com.vrsidekick.models.Provider
import com.vrsidekick.utils.LocationHelper
import com.vrsidekick.utils.setAllOnClickListener
import com.vrsidekick.utils.setSafeOnClickListener
import com.vrsidekick.viewModels.PostsViewModel
import com.vrsidekick.viewModels.ProviderViewModel

private const val TAG = "HomeFragment"

class HomeFragment : Fragment(), ServiceProviderAdapter.IServiceProvider,
    ServiceCategoryHomeAdapter.IServiceCategoryHome, LocationHelper.ILocationHelper {
    private lateinit var binding: FragmentHomeBinding
    private var mServiceCategoryAdapter: ServiceCategoryHomeAdapter? = null
    private var mServiceProviderAdapter: ServiceProviderAdapter? = null
    private val postsViewModel: PostsViewModel by viewModels()
    private val providerViewModel: ProviderViewModel by viewModels()
    private var mIBaseActivity: IBaseActivity? = null
    private var mProviderClickedPosition: Int = -1
    private var mSelectedCategory: Category? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as BaseActivity).getLocationHelper().setOnLocationUpdateListener(this)
        (activity as BaseActivity).getLocationHelper().getCurrentLocation()
        mServiceCategoryAdapter = ServiceCategoryHomeAdapter(requireContext())
        mServiceCategoryAdapter?.setOnServiceCategoryClickListener(this)
        mServiceProviderAdapter = ServiceProviderAdapter()
        mServiceProviderAdapter?.setOnServiceProviderClickListener(this)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: ")
        (activity as BaseActivity).getLocationHelper().getCurrentLocation()

        (activity as BaseActivity).isShowBottomNavBar(true)
        (activity as UserActivity).changeLightStatusBar(R.color.color_background)
        binding.rvServiceCategories.adapter = mServiceCategoryAdapter
        binding.rvServiceProviders.adapter = mServiceProviderAdapter
        setupClickListener()
        setupPostsObserver()
        setupProviderObserver()
        postsViewModel.getServiceCategories(
            "Bearer ${prefs.userToken}"
        )
    }


    private fun setupPostsObserver() {

        postsViewModel.getMessageObserver.observe(viewLifecycleOwner) {
            mIBaseActivity?.showMessage(it)
        }

        postsViewModel.getProgressObserver.observe(viewLifecycleOwner) {
            mIBaseActivity?.showProgressDialog(it)
        }

        postsViewModel.getServiceCategoriesObserver.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                it.first().isSelected = true
                mSelectedCategory = it.first()
                mServiceCategoryAdapter?.submitList(it)
                postsViewModel.getProviderAds(
                    "Bearer ${prefs.userToken}",
                    it.first().id
                )

            }


        }


        postsViewModel.getProviderAdsObserver.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                binding.rvServiceProviders.visibility = View.GONE
                binding.layoutNoData.root.visibility = View.VISIBLE
            } else {
                binding.rvServiceProviders.visibility = View.VISIBLE
                binding.layoutNoData.root.visibility = View.GONE
                mServiceProviderAdapter?.submitList(it)
            }


        }


    }

    private fun setupProviderObserver() {

        providerViewModel.getMessageObserver.observe(viewLifecycleOwner) {
            mIBaseActivity?.showMessage(it)
        }

        providerViewModel.getProgressObserver.observe(viewLifecycleOwner) {
            mIBaseActivity?.showProgressDialog(it)
        }

        providerViewModel.getToggleProviderFavObserver.observe(viewLifecycleOwner) {
            mServiceProviderAdapter?.notifyFavStatus(mProviderClickedPosition, it.favStatus)

        }

        providerViewModel.getSearchProviderObserver.observe(viewLifecycleOwner) {
            val direction = NavGraphDirections.actionGlobalProviderAdsListFragment(it)
            findNavController().navigate(direction)
        }


    }

    private fun setupClickListener() {
        binding.gpLocation.setAllOnClickListener {
            /*  val direction = HomeFragmentDirections.actionNavHomeToSelectLocationFragment()
              findNavController().navigate(direction)*/
            (activity as UserActivity).getLocationHelper().searchPlaces()
        }

        binding.btFilter.setSafeOnClickListener(1000) {
            val direction = HomeFragmentDirections.actionNavHomeToFilterFragment()
            findNavController().navigate(direction)
        }

        binding.etSearch.setOnEditorActionListener { textView, i, keyEvent ->
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                mIBaseActivity?.hideKeyboard()
                validateAndSearchProvider()
                return@setOnEditorActionListener true
            } else {
                return@setOnEditorActionListener false
            }

        }
    }

    private fun validateAndSearchProvider() {
        val keyword = binding.etSearch.text.toString().trim()
        if (keyword.isEmpty()) {
            mIBaseActivity?.showMessage(getString(R.string.messageEnterSearchKeyword))
            return
        } else {
            providerViewModel.searchProvider(
                "Bearer ${prefs.userToken}",
                keyword
            )
        }
    }

    override fun onProviderClick(provider: Provider) {

        mSelectedCategory?.let { category ->
            val direction =
                NavGraphDirections.actionGlobalProviderDetailFragment(provider.id, category.id)
            findNavController().navigate(direction)
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

    override fun onCategorySelect(cateogry: Category) {

        mSelectedCategory = cateogry
        postsViewModel.getProviderAds(
            "Bearer ${prefs.userToken}",
            cateogry.id
        )
    }


    override fun onFavClick(position: Int) {
        mProviderClickedPosition = position
        val provider = mServiceProviderAdapter?.currentList?.get(position)
        provider?.let {
            providerViewModel.toggleProviderFav(
                "Bearer ${prefs.userToken}",
                it.id,
                it.categoryId
            )
        }
    }

    override fun onLocationResult(latLng: LatLng) {
        Log.d(TAG, "onLocationResult: $latLng")
        val locationName =
            (activity as BaseActivity).getLocationHelper().getAddress(requireContext(), latLng)
        binding.tvAddress.text = locationName
    }
}