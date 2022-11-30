package com.vrsidekick.fragments.user

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.vrsidekick.NavGraphDirections
import com.vrsidekick.activities.BaseActivity
import com.vrsidekick.activities.prefs
import com.vrsidekick.adapter.user.PostAdChoosePropertiesAdapter
import com.vrsidekick.databinding.FragmentPostAdChoosePropertyBinding
import com.vrsidekick.interfaces.IBaseActivity
import com.vrsidekick.models.Property
import com.vrsidekick.utils.setSafeOnClickListener
import com.vrsidekick.viewModels.PropertyViewModel

class PostAdChoosePropertyFragment : Fragment(),
    PostAdChoosePropertiesAdapter.IPostAdChooseProperty {
   private lateinit var binding: FragmentPostAdChoosePropertyBinding
    private val propertyViewModel : PropertyViewModel by viewModels()
   private var mPostAdChoosePropertiesAdapter : PostAdChoosePropertiesAdapter?=null
    private var mIBaseActivity : IBaseActivity? =null
    private var mSelectedProperty : Property? =null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPostAdChoosePropertiesAdapter = PostAdChoosePropertiesAdapter(requireContext())
        mPostAdChoosePropertiesAdapter?.setOnChoosePropertyClickListener(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostAdChoosePropertyBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as BaseActivity).isShowBottomNavBar(false)
        binding.rvProperties.adapter = mPostAdChoosePropertiesAdapter
        setupObserver()
      setupListener()
        propertyViewModel.getMyProperties(
            "Bearer ${prefs.userToken}",
        )
    }
    private fun setupObserver() {
        propertyViewModel.getProgressObserver.observe(viewLifecycleOwner){
            mIBaseActivity?.showProgressDialog(it)
        }

        propertyViewModel.getMessageObserver.observe(viewLifecycleOwner){
            mIBaseActivity?.showMessage(it)
        }

        propertyViewModel.getMyPropertyObserver.observe(viewLifecycleOwner){
            if(it.isNotEmpty()){
                it.first().isSelected =true
                mSelectedProperty = it.first()
            }

           mPostAdChoosePropertiesAdapter?.submitList(it)
        }

    }

    private fun setupListener() {
        binding.btAddProperty.setOnClickListener {
            val direction = NavGraphDirections.actionGlobalAddPropertyFragment()
            findNavController().navigate(direction)
        }
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btSubmit.setSafeOnClickListener {
            findNavController().previousBackStackEntry?.savedStateHandle?.set("property_name",mSelectedProperty?.name)
            findNavController().previousBackStackEntry?.savedStateHandle?.set("property_id",mSelectedProperty?.id)
            findNavController().navigateUp()
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

    override fun onPropertyClick(property: Property) {
       mSelectedProperty = property
    }
}
