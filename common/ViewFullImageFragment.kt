package com.vrsidekick.fragments.common

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.vrsidekick.R
import com.vrsidekick.adapter.common.FullImageSlidesAdapter
import com.vrsidekick.databinding.FragmentViewFullImageBinding
import com.vrsidekick.utils.loadFromUrl
import com.vrsidekick.utils.setSafeOnClickListener

private const val TAG ="ViewFullImageFragment"
class ViewFullImageFragment : Fragment() {
    private lateinit var binding : FragmentViewFullImageBinding
    private val navArgs : ViewFullImageFragmentArgs by navArgs()
    private var mFullImageSlides : FullImageSlidesAdapter? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mFullImageSlides = FullImageSlidesAdapter()
        mFullImageSlides?.submitList(navArgs.images.toMutableList())

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentViewFullImageBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.pagerFullImgSlides.adapter = mFullImageSlides
        binding.pagerFullImgSlides.post {
            binding.pagerFullImgSlides.setCurrentItem(navArgs.clickedPosition, true)
        }
        setupClickListener()
    }

    private fun setupClickListener() {
        binding.ivBack.setSafeOnClickListener {
            findNavController().navigateUp()
        }
    }


}