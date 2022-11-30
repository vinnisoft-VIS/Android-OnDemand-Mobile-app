package com.vrsidekick.fragments.user

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.vrsidekick.R
import com.vrsidekick.activities.prefs
import com.vrsidekick.adapter.user.UserReviewsAdapter
import com.vrsidekick.databinding.FragmentAllReviewsUserBinding
import com.vrsidekick.interfaces.IBaseActivity
import com.vrsidekick.viewModels.ProviderViewModel

class AllReviewsUserFragment : Fragment() {
   private lateinit var binding : FragmentAllReviewsUserBinding
    private val providerViewModel: ProviderViewModel by viewModels()
    private val navArgs : AllReviewsUserFragmentArgs by navArgs()
    private var mUserReviewsAdapter: UserReviewsAdapter? = null
    private var mIBaseActivity: IBaseActivity? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mUserReviewsAdapter = UserReviewsAdapter()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAllReviewsUserBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvUserReviews.adapter = mUserReviewsAdapter
        setupListener()
        setupObserver()
        providerViewModel.getReviewsUsers("Bearer ${prefs.userToken}",navArgs.id)


    }

    private fun setupObserver() {
        providerViewModel.getProgressObserver.observe(viewLifecycleOwner) {
                mIBaseActivity?.showProgressDialog(it)
            }

            providerViewModel.getMessageObserver.observe(viewLifecycleOwner) {
                mIBaseActivity?.showMessage(it)
            }
        providerViewModel.getReviewsUsersObserver.observe(viewLifecycleOwner) {
            if (it.reviews.isEmpty()) {
                binding.layoutNoData.root.visibility = View.VISIBLE
                binding.rvUserReviews.visibility = View.GONE
            } else {
                mUserReviewsAdapter?.submitList(it.reviews)
                binding.layoutNoData.root.visibility = View.GONE
                binding.rvUserReviews.visibility = View.VISIBLE

            }
        }

    }

    private fun setupListener() {
        binding.ivBack.setOnClickListener {
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

}