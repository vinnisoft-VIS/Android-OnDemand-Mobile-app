package com.vrsidekick.fragments.user

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.vrsidekick.NavGraphDirections
import com.vrsidekick.R
import com.vrsidekick.activities.BaseActivity
import com.vrsidekick.activities.prefs
import com.vrsidekick.adapter.user.MyPropertyAdapter
import com.vrsidekick.databinding.FragmentMyPropertiesBinding
import com.vrsidekick.dialogs.AddPropertyType
import com.vrsidekick.interfaces.IBaseActivity
import com.vrsidekick.models.Property
import com.vrsidekick.models.ProviderDetail
import com.vrsidekick.utils.setSafeOnClickListener
import com.vrsidekick.viewModels.PropertyViewModel
import com.vrsidekick.viewModels.UploadPropertyData


private const val TAG= "MyPropertiesFragment"
class MyPropertiesFragment : Fragment(), MyPropertyAdapter.IMyPropertyAdapter {
  private lateinit var binding : FragmentMyPropertiesBinding
  private val propertyViewModel : PropertyViewModel by viewModels()
  private var mMyPropertiesAdapter : MyPropertyAdapter? =null
    private var mIBaseActivity : IBaseActivity? =null
    private var mSelectedIndex:Int = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mMyPropertiesAdapter = MyPropertyAdapter()
        mMyPropertiesAdapter?.setOnMyPropertyClickListener(this)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentMyPropertiesBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as BaseActivity).isShowBottomNavBar()
        binding.rvMyProperties.adapter = mMyPropertiesAdapter
        setupClickListener()
        setupObserver()

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
            mMyPropertiesAdapter?.submitList(it)
            setupNoDataView(it.size)

        }

        propertyViewModel.getDeletePropertyObserver.observe(viewLifecycleOwner){
            mMyPropertiesAdapter?.removeItemFromList(mSelectedIndex)
            Log.d(TAG, "setupObserver: delete = ${mMyPropertiesAdapter?.itemCount}")
            mMyPropertiesAdapter?.itemCount?.let {
                setupNoDataView(it-1)
            }

        }

    }

    private fun setupNoDataView(size:Int){
        if(size == 0){
            binding.rvMyProperties.visibility = View.GONE
            binding.layoutNoData.root.visibility = View.VISIBLE
        }else{
            binding.rvMyProperties.visibility = View.VISIBLE
            binding.layoutNoData.root.visibility = View.GONE
        }
    }

    private fun setupClickListener() {
        binding.ivBack.setSafeOnClickListener {
            it.findNavController().navigateUp()
        }

        binding.btAddProperty.setOnClickListener {
            val direction = NavGraphDirections.actionGlobalAddPropertyFragment(false,null)
            findNavController().navigate(direction)
        }
    }

    override fun onItemClick(propertyId:Long) {
        val direction = MyPropertiesFragmentDirections.actionMyPropertiesFragmentToPropertySidekickFragment(propertyId)
        findNavController().navigate(direction)
    }

    override fun onMenuClick(index: Int, view: ImageView) {
        mSelectedIndex = index
        showPopupMenu(index,view)
    }

    private fun showPopupMenu(index: Int, view: ImageView) {

        val popupMenu = PopupMenu(requireContext(),view)
        popupMenu.menuInflater.inflate(R.menu.menu_property,popupMenu.menu)
        popupMenu.setOnMenuItemClickListener {
            when(it.itemId){
                popupMenu.menu.getItem(0).itemId ->{
                    moveToPropertyForm(index)
                    return@setOnMenuItemClickListener true
                }

                popupMenu.menu.getItem(1).itemId -> {
                    deleteProperty(index)
                    return@setOnMenuItemClickListener  true
                }

            }

            return@setOnMenuItemClickListener false
        }
            popupMenu.show()

    }

    private fun moveToPropertyForm(index:Int) {
        val property = mMyPropertiesAdapter?.getProperty(index)
        property?.let {
            val direction = NavGraphDirections.actionGlobalAddPropertyFragment(
                true,it,
            )
            findNavController().navigate(direction)
        }

    }

    private fun deleteProperty(index:Int) {
        val property = mMyPropertiesAdapter?.getProperty(index)
        property?.id?.let { propertyId ->
            propertyViewModel.deleteProperty(
                "Bearer ${prefs.userToken}",
                propertyId)
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
}