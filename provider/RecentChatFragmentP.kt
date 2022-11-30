package com.vrsidekick.fragments.provider

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.vrsidekick.NavGraphDirections
import com.vrsidekick.activities.BaseActivity
import com.vrsidekick.adapter.provider.RecentChatAdapterP
import com.vrsidekick.adapter.user.RecentChatAdapter
import com.vrsidekick.databinding.FragmentRecentChatPBinding


private const val TAG= "RecentChatFragmentP"
class RecentChatFragmentP : Fragment(), RecentChatAdapter.IRecentChat {
    private lateinit var binding : FragmentRecentChatPBinding
    private var mRecentChatAdapter : RecentChatAdapter? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mRecentChatAdapter = RecentChatAdapter(requireContext())
        mRecentChatAdapter?.setOnRecentChatClickListener(this)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecentChatPBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as BaseActivity).isShowBottomNavBar(true)

        binding.rvRecentChats.setHasFixedSize(true)
        binding.rvRecentChats.adapter = mRecentChatAdapter
    }

    override fun onRecentChatClick() {
        val direction = NavGraphDirections.actionGlobalChatFragment()
        findNavController().navigate(direction)
    }
}