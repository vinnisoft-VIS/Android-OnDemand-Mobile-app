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
import com.vrsidekick.NavGraphDirections
import com.vrsidekick.activities.BaseActivity
import com.vrsidekick.activities.prefs
import com.vrsidekick.adapter.user.MySideKickAdapter
import com.vrsidekick.databinding.FragmentMySideKicksBinding
import com.vrsidekick.dialogFragments.MyPropertiesFilterDialogFragment
import com.vrsidekick.interfaces.IBaseActivity
import com.vrsidekick.utils.setSafeOnClickListener
import com.vrsidekick.viewModels.PropertyViewModel


private const val TAG ="MySideKicksFragment"
class MySideKicksFragment : Fragment(), MySideKickAdapter.IMySideKickAdapter,
    MyPropertiesFilterDialogFragment.IMyPropertiesDialogFragment {
    private lateinit var binding : FragmentMySideKicksBinding
    private var mMySideKickAdapter : MySideKickAdapter? =null
    private val propertyViewModel : PropertyViewModel by viewModels()
    private var mIBaseActivity : IBaseActivity? =null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mMySideKickAdapter = MySideKickAdapter(requireContext())
        mMySideKickAdapter?.setOnMySidekickClickListener(this)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMySideKicksBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as BaseActivity).isShowBottomNavBar()
        binding.rvMySidekicks.setHasFixedSize(true)
        binding.rvMySidekicks.adapter = mMySideKickAdapter
        setupPropertyObserver()

        setupClickListener()
    }

    private fun setupPropertyObserver() {
        propertyViewModel.getMessageObserver.observe(viewLifecycleOwner){
            mIBaseActivity?.showMessage(it)
        }

        propertyViewModel.getProgressObserver.observe(viewLifecycleOwner){
            mIBaseActivity?.showProgressDialog(it)
        }

        propertyViewModel.getMySidekicksObserver.observe(viewLifecycleOwner){
            if(it.isEmpty()){
                binding.rvMySidekicks.visibility = View.GONE
                binding.layoutNoData.root.visibility = View.VISIBLE

            }else{
                binding.layoutNoData.root.visibility = View.GONE
                binding.rvMySidekicks.visibility = View.VISIBLE
                mMySideKickAdapter?.submitList(it)
            }

        }
        getMySidekicks()



    }



    private fun  getMySidekicks(propertyId:Long = 0){
        propertyViewModel.getMySidekick(
            "Bearer ${prefs.userToken}",
            propertyId
        )
    }
    private fun setupClickListener() {
        binding.ivBack.setSafeOnClickListener(1000) {
            it.findNavController().navigateUp()
        }

        binding.ivFilter.setSafeOnClickListener {
            val myPropertiesFilterDialog = MyPropertiesFilterDialogFragment.newInstance()
            myPropertiesFilterDialog.setOnMyPropertiesClickListener(this)
            myPropertiesFilterDialog.show(childFragmentManager,TAG)
        }
    }

    override fun onItemClick(bookingId:Long) {
        val direction  = NavGraphDirections.actionGlobalMySidekickDetailFragment(bookingId)
        findNavController().navigate(direction)
    }

    override fun onStop() {
        super.onStop()
        mIBaseActivity?.showProgressDialog(false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mIBaseActivity = context as IBaseActivity
    }

    override fun onDetach() {
        super.onDetach()
        mIBaseActivity = null
    }

    override fun onPropertyClick(propertyId: Long) {
       getMySidekicks(propertyId)
    }
}