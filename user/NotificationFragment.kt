package com.vrsidekick.fragments.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.vrsidekick.activities.BaseActivity
import com.vrsidekick.adapter.user.NotificationAdapter
import com.vrsidekick.databinding.FragmentNotificationBinding
import com.vrsidekick.utils.setSafeOnClickListener

private const val TAG= "NotificationFragment"
class NotificationFragment : Fragment() {
    private lateinit var binding : FragmentNotificationBinding
    private var mNotificationAdapter : NotificationAdapter? =  null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       mNotificationAdapter = NotificationAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotificationBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as BaseActivity).isShowBottomNavBar()
        binding.rvNotifications.setHasFixedSize(true)
        binding.rvNotifications.adapter = mNotificationAdapter
        setupClickListener()
    }

    private fun setupClickListener() {
        binding.ivBack.setSafeOnClickListener(1000) {
            it.findNavController().navigateUp()
        }
    }
}