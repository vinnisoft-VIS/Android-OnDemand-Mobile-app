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
import com.vrsidekick.activities.BaseActivity
import com.vrsidekick.activities.prefs
import com.vrsidekick.adapter.user.MyPropertyAdapter
import com.vrsidekick.adapter.user.MySideKickAdapter
import com.vrsidekick.databinding.FragmentPropertySidekickBinding
import com.vrsidekick.interfaces.IBaseActivity
import com.vrsidekick.models.Property
import com.vrsidekick.utils.Global
import com.vrsidekick.utils.loadFromUrl
import com.vrsidekick.utils.setSafeOnClickListener
import com.vrsidekick.viewModels.PropertyViewModel


private const val TAG ="PropertySidekickFragment"
class PropertySidekickFragment : Fragment(), MySideKickAdapter.IMySideKickAdapter {
   private lateinit var binding : FragmentPropertySidekickBinding
   private val propertyViewModel : PropertyViewModel by viewModels()
    private val navArgs : PropertySidekickFragmentArgs by navArgs()
    private var mIBaseActivity : IBaseActivity? =null

   private var mMySidekickAdapter: MySideKickAdapter? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mMySidekickAdapter = MySideKickAdapter(requireContext())
        mMySidekickAdapter?.setOnMySidekickClickListener(this)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPropertySidekickBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as BaseActivity).isShowBottomNavBar()
        binding.rvMySidekicks.setHasFixedSize(true)
        binding.rvMySidekicks.adapter = mMySidekickAdapter
        setupClickListener()
        propertyViewModel.getPropertySidekickData(
            "Bearer ${prefs.userToken}",
            navArgs.propertyId

        )

        setupPropertyObserver()
    }

    private fun setupPropertyObserver() {
        propertyViewModel.getMessageObserver.observe(viewLifecycleOwner){
            mIBaseActivity?.showMessage(it)

        }
        propertyViewModel.getProgressObserver.observe(viewLifecycleOwner){
            mIBaseActivity?.showProgressDialog(it)
        }
        propertyViewModel.getPropertySidekickDataObserver.observe(viewLifecycleOwner){

            setupPropertyData(it?.property)
            mMySidekickAdapter?.submitList(it?.mySidekicks)
        }

    }

    private fun setupPropertyData(property: Property?) {
        property?.let {
            binding.ivProperty.loadFromUrl(it.image)
            binding.tvPropertyName.text = it.name
            binding.tvPropertyDescription.text = it.description
            binding.tvService.text = String.format("Service: %s",it.categoryName)
          Global.formatDate(it.createdAt)?.let {
              binding.tvListedDate.text = "Listed date: $it"
          }
        }


    }

    private fun setupClickListener() {
        binding.ivBack.setSafeOnClickListener(1000) {
            it.findNavController().navigateUp()
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

    override fun onItemClick(bookingId: Long) {
        val direction = NavGraphDirections.actionGlobalMySidekickDetailFragment(bookingId)
        findNavController().navigate(direction)
    }
}