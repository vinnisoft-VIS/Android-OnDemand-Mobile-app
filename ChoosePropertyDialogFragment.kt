package com.vrsidekick.dialogFragments

import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.vrsidekick.NavGraphDirections
import com.vrsidekick.R
import com.vrsidekick.activities.prefs
import com.vrsidekick.adapter.user.ChoosePropertyAdapter
import com.vrsidekick.databinding.FragmentDialogChoosePropertyBinding
import com.vrsidekick.interfaces.IBaseActivity
import com.vrsidekick.models.Property
import com.vrsidekick.utils.setSafeOnClickListener
import com.vrsidekick.viewModels.PropertyViewModel


private const val TAG = "ChoosePropertyFragment"

class ChoosePropertyDialogFragment : BottomSheetDialogFragment(),
    ChoosePropertyAdapter.IChoosePropertyAdapter {
    private lateinit var binding: FragmentDialogChoosePropertyBinding


    private val propertyViewModel: PropertyViewModel by viewModels()
    private var mIBaseActivity: IBaseActivity? = null
    private var mChoosePropertyAdapter: ChoosePropertyAdapter? = null
    private var mCallback: IChooseProperty? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mChoosePropertyAdapter = ChoosePropertyAdapter()
        mChoosePropertyAdapter?.setOnChoosePropertyClickListener(this)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDialogChoosePropertyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {

            val bottomSheetDialog = it as BottomSheetDialog
            setupFullHeight(bottomSheetDialog)

        }

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        propertyViewModel.getMyProperties(
            "Bearer ${prefs.userToken}"
        )
        binding.rvProperties.adapter = mChoosePropertyAdapter

        setupObserver()
        setupClickListener()



    }


    private fun setupFullHeight(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet = bottomSheetDialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        val layoutParams = bottomSheet.layoutParams

        val windowHeight  =getWindowHeight()

        if(layoutParams != null){
            layoutParams.height = windowHeight-200
        }
        bottomSheet.layoutParams = layoutParams
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetBehavior.isDraggable = false



    }


    private fun getWindowHeight(): Int {
        val outMetrics = DisplayMetrics()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val display = activity?.display
            display?.getRealMetrics(outMetrics)
        } else {
            val display = activity?.windowManager?.defaultDisplay
            display?.getMetrics(outMetrics)
        }
        return outMetrics.heightPixels
    }





    private fun setupObserver() {
        propertyViewModel.getMessageObserver.observe(viewLifecycleOwner) {
            mIBaseActivity?.showMessage(it)
        }

        propertyViewModel.getProgressObserver.observe(viewLifecycleOwner) {
            mIBaseActivity?.showProgressDialog(it)
        }

        propertyViewModel.getMyPropertyObserver.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                binding.rvProperties.visibility = View.GONE
                binding.layoutNoData.root.visibility = View.VISIBLE
            } else {
                mChoosePropertyAdapter?.submitList(it)
                binding.rvProperties.visibility = View.VISIBLE
                binding.layoutNoData.root.visibility = View.GONE

            }

        }
    }

    private fun setupClickListener() {
        binding.ivBack.setSafeOnClickListener {
            dismiss()
        }

        binding.btAddProperty.setOnClickListener {
            val direction = NavGraphDirections.actionGlobalAddPropertyFragment()
            findNavController().navigate(direction)
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


    override fun onItemClick(property: Property) {
        mCallback?.onPropertyClick(property)
        dismiss()
    }


    fun setOnChoosePropertyClickListener(callback: IChooseProperty) {
        mCallback = callback

    }


    interface IChooseProperty {
        fun onPropertyClick(property: Property)
    }


    companion object{
        fun newInstance() = ChoosePropertyDialogFragment()
    }

}