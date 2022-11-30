package com.vrsidekick.fragments.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.vrsidekick.activities.BaseActivity
import com.vrsidekick.adapter.user.RecentSearchedAddressAdapter
import com.vrsidekick.databinding.FragmentSelectLocationBinding
import com.vrsidekick.utils.setSafeOnClickListener

private const val TAG ="SelectLocationFragment"
class SelectLocationFragment : Fragment() {
    private lateinit var binding : FragmentSelectLocationBinding
    private var mRecentSearchedAddressAdapter : RecentSearchedAddressAdapter? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      mRecentSearchedAddressAdapter = RecentSearchedAddressAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSelectLocationBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as BaseActivity).isShowBottomNavBar()
        binding.rvRecentSearchAddress.setHasFixedSize(true)
        binding.rvRecentSearchAddress.adapter = mRecentSearchedAddressAdapter

        setupListener()
    }

    private fun setupListener() {
        binding.ivClose.setSafeOnClickListener(1000) {
            findNavController().navigateUp()
        }
    }
}