package com.vrsidekick.fragments.user

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.vrsidekick.NavGraphDirections
import com.vrsidekick.R
import com.vrsidekick.activities.BaseActivity
import com.vrsidekick.activities.prefs
import com.vrsidekick.adapter.user.ServiceCategoryHomeAdapter
import com.vrsidekick.adapter.user.UserReviewsAdapter
import com.vrsidekick.databinding.FragmentProviderDetailBinding
import com.vrsidekick.dialogFragments.ChooseBookingTimingsDialogFragment
import com.vrsidekick.dialogFragments.ChooseBookingPricingTypeDialogFragment
import com.vrsidekick.dialogFragments.ChooseCardDialogFragment
import com.vrsidekick.dialogFragments.ChoosePropertyDialogFragment
import com.vrsidekick.interfaces.IBaseActivity
import com.vrsidekick.models.Card
import com.vrsidekick.models.Property
import com.vrsidekick.models.ProviderDetail
import com.vrsidekick.models.Reviews
import com.vrsidekick.utils.PricingType
import com.vrsidekick.utils.loadFromUrl
import com.vrsidekick.utils.setSafeOnClickListener
import com.vrsidekick.viewModels.ProviderViewModel
import java.lang.StringBuilder
import kotlin.math.abs

private const val TAG = "ProviderDetailFragments"

class ProviderDetailFragment : Fragment(), ChoosePropertyDialogFragment.IChooseProperty,
    ChooseBookingPricingTypeDialogFragment.IChoosePricingType,
    ChooseBookingTimingsDialogFragment.IChooseBookingTimings,
    ChooseCardDialogFragment.IChooseCardFrag {
    private lateinit var binding: FragmentProviderDetailBinding
    private val providerViewModel: ProviderViewModel by viewModels()
    private val navArgs: ProviderDetailFragmentArgs by navArgs()
    private var mServiceCategoryAdapter: ServiceCategoryHomeAdapter? = null
    private var mUserReviewsAdapter: UserReviewsAdapter? = null
    private var mIBaseActivity: IBaseActivity? = null
    private var mProviderDetail: ProviderDetail? = null
    private var mSelectedProperty: Property? = null
    private var mSelectedCard: Card? = null
    private var mSelectedPricingType: PricingType? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mServiceCategoryAdapter = ServiceCategoryHomeAdapter(requireContext())
        mUserReviewsAdapter = UserReviewsAdapter()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProviderDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as BaseActivity).isShowBottomNavBar()
        providerViewModel.getProviderDetail(
            "Bearer ${prefs.userToken}",
            navArgs.providerId,
            navArgs.categoryId
        )



        binding.rvServiceCategories.adapter = mServiceCategoryAdapter
        binding.rvUserReviews.adapter = mUserReviewsAdapter
        setupClickListener()
        setupObserver()

    }

    private fun setupObserver() {
        providerViewModel.getProgressObserver.observe(viewLifecycleOwner) {
            mIBaseActivity?.showProgressDialog(it)
        }

        providerViewModel.getMessageObserver.observe(viewLifecycleOwner) {
            mIBaseActivity?.showMessage(it)
        }


        providerViewModel.getToggleProviderFavObserver.observe(viewLifecycleOwner) {
            setFav(it.favStatus)

        }

        providerViewModel.getProviderDetailObserver.observe(viewLifecycleOwner) {

            setupUi(it)
        }
    }

    private fun setupUi(detail: ProviderDetail?) {

        mProviderDetail = detail

        mProviderDetail?.let { provider ->

            if (provider.allReviews.isEmpty()) {
                binding.btShowMore.visibility = View.GONE
            } else {
                binding.btShowMore.visibility = View.VISIBLE
                mUserReviewsAdapter?.submitList(provider.allReviews)
            }

            binding.ivProvider.loadFromUrl(provider.profilePhotoUrl)
            binding.tvProviderName.text = provider.name
            binding.tvRating.text = provider.ratingAvg
            binding.tvReview.text = "(${provider.ratingCount} Reviews)"
            binding.tvPrice.text = String.format(
                "$%s/hr,$%s/mth",
                provider.hourlyRate ?: "0",
                provider.monthlyRate ?: "0"
            )
            binding.tvDistance.text = String.format("%s km", provider.serviceInKm ?: "1")
            setFav(provider.isFav ?: 0)
            binding.tvDescription.text = provider.skill
            binding.tvAvailability.text = getAvailabilityString(provider.days)
            binding.tvToolsAvailable.text = provider.toolsAvailable
            binding.tvSupplyAvailable.text = provider.supplyAvailable
            binding.tvReview1.text = "(${provider.reviewCount} Reviews)"
            binding.tvRatingCount.text = provider.ratingAvg.toString()
            binding.tvReviewCountRating1.text = "(" + provider.reviewOne.toString() + ")"
            binding.tvReviewCountRating2.text = "(" + provider.reviewTwo.toString() + ")"
            binding.tvReviewCountRating3.text = "(" + provider.reviewThree.toString() + ")"
            binding.tvReviewCountRating4.text = "(" + provider.reviewFour.toString() + ")"
            binding.tvReviewCountRating5.text = "(" + provider.reviewFive.toString() + ")"
            binding.progressBarRating1.progress = abs(provider.reviewOne!!)
            binding.progressBarRating2.progress = Math.abs(provider.reviewTwo!!)
            binding.progressBarRating3.progress = Math.abs(provider.reviewThree!!)
            binding.progressBarRating4.progress = Math.abs(provider.reviewFour!!)
            binding.progressBarRating5.progress = Math.abs(provider.reviewFive!!)
            mServiceCategoryAdapter?.submitList(provider.categories)


        }


    }

    private fun setFav(status: Int) {
        if (status == 1) {
            binding.ivLike.setImageResource(R.drawable.ic_heart_filled)
        } else {
            binding.ivLike.setImageResource(R.drawable.ic_heart_empty)
        }
    }

    private fun getAvailabilityString(days: ArrayList<Int>): String? {
        if (days.isEmpty()) return null


        if (days.size >= 7) {
            return "Full week"
        } else {

            val daysBuilder = StringBuilder()
            if (0 in days) {
                daysBuilder.append("Sun")
                daysBuilder.append(",")
            }

            if (1 in days) {
                daysBuilder.append("Mon")
                daysBuilder.append(",")
            }

            if (2 in days) {
                daysBuilder.append("Tue")
                daysBuilder.append(",")
            }

            if (3 in days) {
                daysBuilder.append("Wed")
                daysBuilder.append(",")
            }

            if (4 in days) {
                daysBuilder.append("Thu")
                daysBuilder.append(",")
            }

            if (5 in days) {
                daysBuilder.append("Fri")
                daysBuilder.append(",")
            }

            if (6 in days) {
                daysBuilder.append("Sat")
                daysBuilder.append(",")
            }

            return daysBuilder.substring(0, daysBuilder.length - 1)


        }


    }

    private fun setupClickListener() {
        binding.ivBack.setSafeOnClickListener(1000) {
            it.findNavController().navigateUp()
        }


        binding.ivProvider.setOnClickListener {
            mProviderDetail?.profilePhotoUrl?.let { providerImg ->
                val direction = NavGraphDirections.actionGlobalViewFullImageProviderProfileFragment(
                    arrayOf(providerImg)
                )
                findNavController().navigate(direction)


            }
        }

        binding.btShowMore.setOnClickListener {
            mProviderDetail?.id?.let {
                val directions =
                    ProviderDetailFragmentDirections.actionProviderDetailFragmentToAllReviewsUserFragment(
                        it
                    )
                findNavController().navigate(directions)
            }

        }


        binding.ivLike.setSafeOnClickListener(1000) {

            mProviderDetail?.let {
                setFav(if (it.isFav == 0) 1 else 0)
                providerViewModel.toggleProviderFav(
                    "Bearer ${prefs.userToken}",
                    it.id,
                    navArgs.categoryId
                )
            }
        }

        binding.btBook.setSafeOnClickListener {
            val choosePropertyDialogFragment = ChoosePropertyDialogFragment.newInstance()
            choosePropertyDialogFragment.setOnChoosePropertyClickListener(this)
            choosePropertyDialogFragment.show(childFragmentManager, TAG)
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
        choosePaymentCard()

    }


    private fun choosePaymentCard() {
        val chooseCardDialog = ChooseCardDialogFragment.newInstance()
        chooseCardDialog.setOnCardSelectListener(this)
        chooseCardDialog.show(childFragmentManager, TAG)
    }


    private fun choosePricingType() {
        val choosePricingTypeDialogFragment = ChooseBookingPricingTypeDialogFragment.newInstance()
        choosePricingTypeDialogFragment.setOnChoosePricingTypeListener(this)
        choosePricingTypeDialogFragment.show(childFragmentManager, TAG)
    }

    override fun onPricingTypeSelect(pricingType: PricingType) {
        mSelectedPricingType = pricingType
        chooseBookingDurationType()
    }


    private fun chooseBookingDurationType() {
        val chooseBookingTimingsDialogFragment =
            ChooseBookingTimingsDialogFragment.newInstance(navArgs.providerId, navArgs.categoryId)
        chooseBookingTimingsDialogFragment.setOnChooseBookingTimingListener(this)
        chooseBookingTimingsDialogFragment.show(childFragmentManager, TAG)
    }

    override fun onChooseDuration(date: String, time: String) {

        Log.d(TAG, "onChooseDuration: date  $date")
        Log.d(TAG, "onChooseDuration: time  $time")
        Log.d(TAG, "onChooseDuration: selected property id  ${mSelectedProperty?.id}")
        Log.d(TAG, "onChooseDuration: selected pricing type  ${mSelectedPricingType?.name}")
        Log.d(TAG, "onChooseDuration: category_id  ${navArgs.categoryId}")


        val direction =
            ProviderDetailFragmentDirections.actionProviderDetailFragmentToBookingConfirmationFragment(
                mProviderDetail ?: return,
                mSelectedPricingType ?: return,
                date,
                time,
                mSelectedProperty?.id ?: return,
                navArgs.categoryId
            )

        findNavController().navigate(direction)


    }

    override fun onCardSelect(card: Card) {
        mSelectedCard = card
        choosePricingType()
    }
}