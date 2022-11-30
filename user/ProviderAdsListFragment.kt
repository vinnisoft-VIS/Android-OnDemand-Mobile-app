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
import com.vrsidekick.NavGraphDirections
import com.vrsidekick.R
import com.vrsidekick.activities.BaseActivity
import com.vrsidekick.activities.prefs
import com.vrsidekick.adapter.user.ServiceProviderAdapter
import com.vrsidekick.databinding.FragmentProviderAdsListBinding
import com.vrsidekick.interfaces.IBaseActivity
import com.vrsidekick.models.Category
import com.vrsidekick.models.Provider
import com.vrsidekick.utils.setSafeOnClickListener
import com.vrsidekick.viewModels.PostsViewModel
import com.vrsidekick.viewModels.ProviderViewModel

private const val TAG ="ProviderAdsListFragment"
class ProviderAdsListFragment : Fragment(), ServiceProviderAdapter.IServiceProvider {
    private lateinit var binding : FragmentProviderAdsListBinding
    private val providerViewModel : ProviderViewModel by viewModels()
    private val navArgs : ProviderAdsListFragmentArgs by navArgs()
    private var mServiceProviderAdapter: ServiceProviderAdapter? = null
    private var mProviderClickedPosition:Int = -1
    private var mSelectedCategory : Category? =null
    private var mIBaseActivity: IBaseActivity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mServiceProviderAdapter = ServiceProviderAdapter()
        mServiceProviderAdapter?.setOnServiceProviderClickListener(this)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProviderAdsListBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as BaseActivity).isShowBottomNavBar()
        binding.rvServiceProviders.adapter = mServiceProviderAdapter
        setupListData()

        setupClickListener()
        setupProviderObserver()
    }

    private fun setupListData() {
        val providers = navArgs.providers.providers
        if (providers.isEmpty()) {
            binding.rvServiceProviders.visibility = View.GONE
            binding.layoutNoData.root.visibility = View.VISIBLE
        } else {
            binding.rvServiceProviders.visibility = View.VISIBLE
            binding.layoutNoData.root.visibility = View.GONE
            mServiceProviderAdapter?.submitList(providers)
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
            mServiceProviderAdapter?.notifyFavStatus(mProviderClickedPosition,it.favStatus)

        }





    }

    private fun setupClickListener() {
        binding.ivBack.setSafeOnClickListener(1000) {
            it.findNavController().navigateUp()
        }
    }





    private fun viewProviderDetail() {

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

    override fun onProviderClick(provider: Provider) {
      //  val direction = NavGraphDirections.actionGlobalProviderDetailFragment()
      //  findNavController().navigate(direction)
    }

    override fun onFavClick(position:Int) {
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
}