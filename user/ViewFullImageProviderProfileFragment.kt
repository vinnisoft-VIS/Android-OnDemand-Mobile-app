package com.vrsidekick.fragments.user

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.vrsidekick.R
import com.vrsidekick.activities.BaseActivity
import com.vrsidekick.adapter.common.FullImageSlidesAdapter
import com.vrsidekick.databinding.FragmentViewFullImageProviderProfileBinding
import com.vrsidekick.utils.loadFromUrl

class ViewFullImageProviderProfileFragment : Fragment() {
private lateinit var binding : FragmentViewFullImageProviderProfileBinding
private val navArgs : ViewFullImageProviderProfileFragmentArgs by navArgs()
    private var mFullImageSlides : FullImageSlidesAdapter? =null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mFullImageSlides = FullImageSlidesAdapter()
        mFullImageSlides?.submitList(navArgs.providerImage.toMutableList())

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentViewFullImageProviderProfileBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as BaseActivity).isShowBottomNavBar(false)
      //  binding.ivPostImage.loadFromUrl(navArgs.providerImage)
        binding.pagerFullImg.adapter = mFullImageSlides

        setupListener()
    }

    private fun setupListener() {

        binding.ivClose.setOnClickListener {
            findNavController().navigateUp()
        }
    }
}