package com.vrsidekick.dialogFragments

import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.vrsidekick.NavGraphDirections
import com.vrsidekick.R
import com.vrsidekick.activities.prefs
import com.vrsidekick.adapter.common.ChooseCardAdapter
import com.vrsidekick.databinding.FragmentChooseCardDialogBinding
import com.vrsidekick.interfaces.IBaseActivity
import com.vrsidekick.models.Card
import com.vrsidekick.utils.Constants
import com.vrsidekick.utils.setSafeOnClickListener
import com.vrsidekick.viewModels.AccountViewModel
import com.vrsidekick.viewModels.ManagePaymentViewModel

private const val TAG ="ChooseCardDialogFragment"
class ChooseCardDialogFragment : BottomSheetDialogFragment(), ChooseCardAdapter.IChooseCard {
    private lateinit var binding : FragmentChooseCardDialogBinding
    private var mChooseCardAdapter : ChooseCardAdapter? =null
    private val managePaymentViewModel : ManagePaymentViewModel by viewModels()
    private var mIBaseActivity : IBaseActivity? =null
    private var mCallback : IChooseCardFrag? =null
    private var mSelectedCard:Card? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mChooseCardAdapter = ChooseCardAdapter()
        mChooseCardAdapter?.setOnChooseCardClickListener(this)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
         binding = FragmentChooseCardDialogBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvCards.adapter = mChooseCardAdapter
        setupObserver()
        setupClickListener()
        getCards()
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

    private fun setupClickListener() {
        binding.ivBack.setSafeOnClickListener {
            dismiss()
        }

        binding.btAddNewCard.setSafeOnClickListener {
            val direction = NavGraphDirections.actionGlobalAddCardFragment()
            findNavController().navigate(direction)
        }

        binding.btChooseCard.setSafeOnClickListener {
            mSelectedCard?.let {
                dismiss()
                mCallback?.onCardSelect(it)
            }


        }
    }



    private fun getCards() {
        managePaymentViewModel.getCards(
            "Bearer ${prefs.userToken}",
            Constants.HEADER_X_REQUESTED_WITH,

            )
    }

    private fun setupObserver() {
        managePaymentViewModel.getProgressObserver.observe(viewLifecycleOwner){
            mIBaseActivity?.showProgressDialog(it)
        }

        managePaymentViewModel.getMessageObserver.observe(viewLifecycleOwner){
            mIBaseActivity?.showMessage(it)
        }

        managePaymentViewModel.getCardsObserver.observe(viewLifecycleOwner){
            it.first().isSelected= true
            mChooseCardAdapter?.submitList(it)

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

    override fun onCardClick(card: Card) {
        binding.btChooseCard.isEnabled =true
        mSelectedCard = card
    }


    fun  setOnCardSelectListener(callback: IChooseCardFrag){
        mCallback = callback
    }



    interface  IChooseCardFrag{
        fun onCardSelect(card:Card)
    }

    companion object{

        fun newInstance() = ChooseCardDialogFragment()
    }



}