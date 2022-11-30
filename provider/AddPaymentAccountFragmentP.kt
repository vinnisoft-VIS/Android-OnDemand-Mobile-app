package com.vrsidekick.fragments.provider

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.vrsidekick.R
import com.vrsidekick.activities.BaseActivity
import com.vrsidekick.databinding.FragmentAddPaymentAccountPBinding
import com.vrsidekick.utils.setSafeOnClickListener

private const val TAG= "AddPaymentAccountFragmentP"
class AddPaymentAccountFragmentP : Fragment() {
    private lateinit var binding : FragmentAddPaymentAccountPBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddPaymentAccountPBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as BaseActivity).isShowBottomNavBar()
        setupClickListener()
    }

    private fun setupClickListener() {
        binding.ivBack.setSafeOnClickListener(1000) {
            it.findNavController().navigateUp()
        }
    }
}