package com.vrsidekick.fragments.provider

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.vrsidekick.R
import com.vrsidekick.activities.BaseActivity
import com.vrsidekick.adapter.provider.NotificationAdapterP
import com.vrsidekick.databinding.FragmentNotificationPBinding
import com.vrsidekick.utils.setSafeOnClickListener

private const val TAG= "NotificationFragmentP"
class NotificationFragmentP : Fragment() {
   private lateinit var binding : FragmentNotificationPBinding
   private var mNotificationAdapter : NotificationAdapterP? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mNotificationAdapter = NotificationAdapterP()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotificationPBinding.inflate(inflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvNotifications.setHasFixedSize(true)
        binding.rvNotifications.adapter = mNotificationAdapter
        (activity as BaseActivity).isShowBottomNavBar()
        setupClickListener()
    }

    private fun setupClickListener() {
        binding.ivBack.setSafeOnClickListener(1000) {
            it.findNavController().navigateUp()
        }
    }
}