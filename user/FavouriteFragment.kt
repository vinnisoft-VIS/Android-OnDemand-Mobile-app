package com.vrsidekick.fragments.user

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.vrsidekick.NavGraphDirections
import com.vrsidekick.activities.BaseActivity
import com.vrsidekick.activities.prefs
import com.vrsidekick.adapter.user.FavouriteAdapter
import com.vrsidekick.adapter.user.ServiceProviderAdapter
import com.vrsidekick.databinding.FragmentFavouriteBinding
import com.vrsidekick.interfaces.IBaseActivity
import com.vrsidekick.models.Provider
import com.vrsidekick.utils.setSafeOnClickListener
import com.vrsidekick.viewModels.PostsViewModel
import com.vrsidekick.viewModels.ProviderViewModel

private const val TAG = "FavouriteFragment"

class FavouriteFragment : Fragment(), ServiceProviderAdapter.IServiceProvider {
    private lateinit var binding: FragmentFavouriteBinding
    private val postsViewModel: PostsViewModel by viewModels()
    private val providerViewModel: ProviderViewModel by viewModels()
    private var mIBaseActivity: IBaseActivity? = null
    private var mServiceProviderAdapter: ServiceProviderAdapter? = null
    private var mProviderClickedPosition: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mServiceProviderAdapter = ServiceProviderAdapter()
        mServiceProviderAdapter?.setOnServiceProviderClickListener(this)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavouriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as BaseActivity).isShowBottomNavBar()
        binding.rvFavourites.setHasFixedSize(true)
        binding.rvFavourites.adapter = mServiceProviderAdapter
        setupClickListener()
        providerViewModel.getFavProvider(
            "Bearer ${prefs.userToken}"
        )

        setupProviderObserver()
    }


    private fun setupProviderObserver() {

        providerViewModel.getMessageObserver.observe(viewLifecycleOwner) {
            mIBaseActivity?.showMessage(it)
        }

        providerViewModel.getProgressObserver.observe(viewLifecycleOwner) {
            mIBaseActivity?.showProgressDialog(it)
        }

        providerViewModel.getToggleProviderFavObserver.observe(viewLifecycleOwner) {
            mServiceProviderAdapter?.removeItem(mProviderClickedPosition)
            Handler(Looper.getMainLooper()).postDelayed({
                if(mServiceProviderAdapter?.currentList?.isEmpty() == true){
                    binding.rvFavourites.visibility = View.GONE
                    binding.layoutNoData.root.visibility = View.VISIBLE
                }else{

                    binding.rvFavourites.visibility = View.VISIBLE
                    binding.layoutNoData.root.visibility = View.GONE
                }
           },2000

            )

        }

        providerViewModel.getFavProvidersObserver.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                binding.rvFavourites.visibility = View.GONE
                binding.layoutNoData.root.visibility = View.VISIBLE
            } else {
                binding.rvFavourites.visibility = View.VISIBLE
                binding.layoutNoData.root.visibility = View.GONE
                mServiceProviderAdapter?.submitList(it)
            }



        }









    }

private fun setupClickListener() {
    binding.ivBack.setSafeOnClickListener(1000) {
        it.findNavController().navigateUp()
    }
}

override fun onProviderClick(provider: Provider) {
    /* mSelectedCategory?.let {category->
         val direction = NavGraphDirections.actionGlobalProviderDetailFragment(provider.id,category.id)
         findNavController().navigate(direction)
     }*/

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

override fun onAttach(context: Context) {
    super.onAttach(context)
    mIBaseActivity = context as IBaseActivity
}

override fun onDetach() {
    super.onDetach()
    mIBaseActivity = null
}
}