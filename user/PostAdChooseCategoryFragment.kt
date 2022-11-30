package com.vrsidekick.fragments.user

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.vrsidekick.R
import com.vrsidekick.activities.BaseActivity
import com.vrsidekick.activities.UserActivity
import com.vrsidekick.activities.prefs
import com.vrsidekick.adapter.provider.ServiceCatArrayAdapter
import com.vrsidekick.databinding.FragmentPostAdChooseCategoryBinding
import com.vrsidekick.interfaces.IBaseActivity
import com.vrsidekick.models.Category
import com.vrsidekick.utils.setSafeOnClickListener
import com.vrsidekick.viewModels.PostsViewModel
import com.vrsidekick.viewModels.PropertyViewModel


class PostAdChooseCategoryFragment : Fragment() {
    private lateinit var binding: FragmentPostAdChooseCategoryBinding
    private val propertyViewModel: PropertyViewModel by activityViewModels()
    private var mSelectedCateogry: Category? = null
    private var mIBaseActivity: IBaseActivity? = null
    private val postsViewModel: PostsViewModel by viewModels()
    private var mServiceCategoryAdapter: ServiceCatArrayAdapter? = null
    private var mPropertyName: String? = null
    private var mPropertyId: Long? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mServiceCategoryAdapter =
            ServiceCatArrayAdapter(requireContext(), android.R.layout.simple_list_item_1)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostAdChooseCategoryBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as BaseActivity).isShowBottomNavBar(false)
        setupListener()
        setupPostObserver()
        getServiceCategories()
        observeReturnedData()

        // val uploadPropertyData = propertyViewModel.getPropertyData()
        //  mSelectedService = uploadPropertyData.service
    }


    private fun observeReturnedData() {

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Long>("property_id")
            ?.observe(viewLifecycleOwner) {
                it?.let {

                    mPropertyId = it
                }
            }

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>("property_name")
            ?.observe(viewLifecycleOwner) {
                it?.let {
                    mPropertyName = it
                    binding.btSelectProperty.text = mPropertyName

                }
            }
    }

    private fun setupListener() {
        binding.btSelectProperty.setOnClickListener {
            val direction =
                PostAdChooseCategoryFragmentDirections.actionNavPostAdToPostAdSelectPropertyFragment()
            findNavController().navigate(direction)
        }
        binding.ivClose.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btPostAd.setSafeOnClickListener {
            validateData()
        }
    }

    private fun validateData() {
        if(mSelectedCateogry == null){
            mIBaseActivity?.showMessage(getString(R.string.messageSelectServiceCategory))
            return
        }

        if(mPropertyId ==null){
            mIBaseActivity?.showMessage(getString(R.string.messageSelectproperty))
            return
        }

        postsViewModel.addPost(
            "Bearer ${prefs.userToken}",
            mSelectedCateogry!!.id,
            mPropertyId!!
        )

    }

    private fun setupPostObserver() {
        postsViewModel.getProgressObserver.observe(viewLifecycleOwner) {
            mIBaseActivity?.showProgressDialog(it)
        }

        postsViewModel.getMessageObserver.observe(viewLifecycleOwner) {
            mIBaseActivity?.showMessage(it)
        }

        postsViewModel.getServiceCategoriesObserver.observe(viewLifecycleOwner) {
            mServiceCategoryAdapter?.setData(it)
            binding.autocompleteEtService.setAdapter(mServiceCategoryAdapter)

            binding.autocompleteEtService.setOnItemClickListener { adapterView, view, i, l ->
                mIBaseActivity?.hideKeyboard()
                val category = mServiceCategoryAdapter?.getItem(i)
                mSelectedCateogry = category
                category?.let {
                    binding.autocompleteEtService.setText(it.name)
                }
            }

        }

        postsViewModel.getAddPostObserver.observe(viewLifecycleOwner){
            MaterialAlertDialogBuilder(requireContext(),R.style.MaterialAlertDialog_App)
                .setTitle(getString(R.string.success))
                .setMessage(it.message)
                .setPositiveButton(getString(R.string.dismiss)){dialog,which ->
                    dialog.dismiss()
                    findNavController().navigateUp()
                }
                .show()
        }
    }

    private fun getServiceCategories() {

        postsViewModel.getServiceCategories("Bearer ${prefs.userToken}")

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