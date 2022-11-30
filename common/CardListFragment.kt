package com.vrsidekick.fragments.common

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.vrsidekick.NavGraphDirections
import com.vrsidekick.R
import com.vrsidekick.activities.BaseActivity
import com.vrsidekick.activities.prefs
import com.vrsidekick.adapter.common.CardListAdapter
import com.vrsidekick.databinding.FragmentCardListBinding
import com.vrsidekick.interfaces.IBaseActivity
import com.vrsidekick.models.Card
import com.vrsidekick.utils.Constants
import com.vrsidekick.utils.setSafeOnClickListener
import com.vrsidekick.viewModels.AccountViewModel
import com.vrsidekick.viewModels.ManagePaymentViewModel

private const val TAG = "MyCardsFragment"
class CardListFragment : Fragment(), CardListAdapter.ICardList {
    private lateinit var binding : FragmentCardListBinding
    private var mCardListAdapter : CardListAdapter? =null
    private val managePaymentViewModel : ManagePaymentViewModel by viewModels()
    private var mIBaseActivity : IBaseActivity? =null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mCardListAdapter = CardListAdapter()
        mCardListAdapter?.setOnCardListClickListener(this)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCardListBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as BaseActivity).isShowBottomNavBar()
        binding.rvCards.adapter = mCardListAdapter
        getCards()
        setupObserver()
        setupClickListener()
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
            if(it.isEmpty()){
                binding.rvCards.visibility = View.GONE
                binding.layoutNoData.root.visibility = View.VISIBLE
            }else{
                mCardListAdapter?.submitList(it)
                binding.rvCards.visibility = View.VISIBLE
                binding.layoutNoData.root.visibility = View.GONE

            }

        }
        managePaymentViewModel.getDeleteCardObserver.observe(viewLifecycleOwner){
            getCards()
        }
    }

    private fun setupClickListener() {
        binding.ivBack.setSafeOnClickListener(1000) {
            it.findNavController().navigateUp()
        }

        binding.btAddNewCard.setSafeOnClickListener(1000) {
            val direction = NavGraphDirections.actionGlobalAddCardFragment()
            it.findNavController().navigate(direction)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mIBaseActivity = context as IBaseActivity
    }

    override fun onDetach() {
        super.onDetach()
        mIBaseActivity =null
    }

    override fun onRemoveClick(card: Card) {
        card.cardId?.let {
            MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialog_App)
                .setTitle(getString(R.string.alert))
                .setMessage(getString(R.string.messageWantToRemoveCard))
                .setPositiveButton(getString(R.string.remove)){dialog,which -> removeCard(it)}
                .setNegativeButton(getString(R.string.dismiss)){dialog,which -> dialog.dismiss()}
                .show()
        }

    }

    private fun removeCard(cardId: String) {
        managePaymentViewModel.deleteCard(
            "Bearer ${prefs.userToken}",
            Constants.HEADER_X_REQUESTED_WITH,
            cardId
        )

    }
}