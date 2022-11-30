package com.vrsidekick.fragments.user

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.vrsidekick.NavGraphDirections
import com.vrsidekick.R
import com.vrsidekick.activities.BaseActivity
import com.vrsidekick.activities.prefs
import com.vrsidekick.adapter.user.PropertyImagesAdapter
import com.vrsidekick.databinding.FragmentMySidekickDetailBinding
import com.vrsidekick.interfaces.IBaseActivity
import com.vrsidekick.models.SidekickDetail
import com.vrsidekick.utils.Global
import com.vrsidekick.utils.SizeConfig
import com.vrsidekick.utils.loadFromUrl
import com.vrsidekick.utils.setSafeOnClickListener
import com.vrsidekick.viewModels.PropertyViewModel

private const val TAG = "MySidekickDetailFragment"
private const val API_EXECUTION_DELAY = 5 * 1000L

class MySidekickDetailFragment : Fragment(), PropertyImagesAdapter.IPropertyImage {
    private lateinit var binding: FragmentMySidekickDetailBinding
    private val propertyViewModel: PropertyViewModel by viewModels()
    private val navArgs: MySidekickDetailFragmentArgs by navArgs()
    private var mIBaseActivity: IBaseActivity? = null
    private var mPropertyImagesAdapter: PropertyImagesAdapter? = null
    private var mWaitingDialog: Dialog? = null
    private var mConfirmWorkRateDialog: Dialog? = null
    private var isLoading = true
    private var mSidekickDetail: SidekickDetail? = null
    private val mHandler = Handler(Looper.getMainLooper())
    private val mRunnable = object : Runnable {
        override fun run() {
            getSidekickDetail()
            mHandler.postDelayed(this, API_EXECUTION_DELAY)
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPropertyImagesAdapter = PropertyImagesAdapter()
        mPropertyImagesAdapter?.setOnPropertyImageClickListener(this)
        createWaitingDialog()
        createConfirmWorkRateDialog()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentMySidekickDetailBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onStart() {
        super.onStart()
        mHandler.postDelayed(mRunnable, API_EXECUTION_DELAY)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as BaseActivity).isShowBottomNavBar()
        binding.rvPropertyImages.setHasFixedSize(true)
        binding.rvPropertyImages.adapter = mPropertyImagesAdapter
        getSidekickDetail()

        setupClickListener()
        setupPropertyObserver()


    }


    private fun getSidekickDetail() {
        propertyViewModel.getSidekickDetail(
            "Bearer ${prefs.userToken}",
            navArgs.bookingId
        )
    }

    private fun setupPropertyObserver() {
        propertyViewModel.getProgressObserver.observe(viewLifecycleOwner) {
            if (mSidekickDetail != null) {
                mIBaseActivity?.showProgressDialog(false)
            } else {
                mIBaseActivity?.showProgressDialog(it)
            }


        }

        propertyViewModel.getMessageObserver.observe(viewLifecycleOwner) {
            mIBaseActivity?.showMessage(it)
        }

        propertyViewModel.getSidekickDetailObserver.observe(viewLifecycleOwner) {
            setupUi(it)
        }

        propertyViewModel.getCancelBookingObserver.observe(viewLifecycleOwner) {
            hideConfirmWorkRateDialog()
            findNavController().navigateUp()
        }

        propertyViewModel.getConfirmWorkingRatesObserver.observe(viewLifecycleOwner) {
            getSidekickDetail()
        }
    }

    private fun setupUi(detailData: SidekickDetail?) {
        mSidekickDetail = detailData
        detailData?.let { detail ->
            binding.ivProperty.loadFromUrl(detail.propertyImage)
            binding.tvPropertyName.text = detail.propertyName
            binding.ivProvider.loadFromUrl(detail.providerImage)
            binding.tvProviderName.text = detail.providerName

            setupWorkStatus(detail)


            binding.tvService.text = detail.categoryName
            Global.formatDate(detail.createdAt)?.let {
                binding.tvDate.text = it
            }
            binding.tvBookingTime.text = detail.time
            binding.tvScheduleType.text = detail.rateType
            binding.tvPrice.text = detail.rate
            binding.tvTimeDuration.text = detail.timeDuration
            mPropertyImagesAdapter?.submitList(detail.workDoneImages)


            if(detailData.status ==0){
                if (detailData.isApproved == 0) {
                    showWaitingDialog()
                } else {
                    hideWaitingDialog()
                }

                if (detailData.isApproved == 1) {
                    mConfirmWorkRateDialog?.findViewById<TextView>(R.id.tv_title)?.text =
                        "${getString(R.string.confirmWorkingRate)}\n$${mSidekickDetail?.newRate}"
                    showConfirmWorkRateDialog()
                } else {
                    hideConfirmWorkRateDialog()
                }

            }else{
                mHandler.removeCallbacks(mRunnable)
            }


        }
    }

    private fun setupWorkStatus(detail: SidekickDetail) {

        if (detail.status == 0) {
            binding.tvWorkStatus.text = getString(R.string.inProgress)
            binding.llRootBtPay.visibility = View.GONE
            binding.tvWorkStatus.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_text_primary
                )
            )
        } else if (detail.status == 1) {
            binding.llRootBtPay.visibility = View.VISIBLE
            binding.tvWorkStatus.text = getString(R.string.completed)
            binding.tvWorkStatus.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_green_book
                )
            ) }
             else {
            binding.llRootBtPay.visibility = View.GONE
                binding.tvWorkStatus.text = getString(R.string.cancelled)
                binding.tvWorkStatus.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.color_red
                    )
                )
            }

    }

    private fun setupClickListener() {
        binding.ivBack.setSafeOnClickListener(1000) {
            it.findNavController().navigateUp()
        }
        binding.btPay.setOnClickListener {
            val direction = MySidekickDetailFragmentDirections.actionMySidekickDetailFragmentToPaymentDetailFragment(navArgs.bookingId)
            findNavController().navigate(direction)
        }


    }

    private fun cancelBooking() {
        propertyViewModel.cancelBooking(
            "Bearer ${prefs.userToken}",
            navArgs.bookingId
        )
    }

    private fun confirmWorkingRates() {
        propertyViewModel.confirmWorkingRates(
            "Bearer ${prefs.userToken}",
            navArgs.bookingId
        )
    }

    private fun createWaitingDialog() {
        mWaitingDialog = Dialog(requireContext())
        mWaitingDialog?.setContentView(R.layout.dialog_waiting_loader_provider)
        mWaitingDialog?.window?.setLayout(
            SizeConfig.screenWidth - 150,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        mWaitingDialog?.window?.setBackgroundDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.bg_dialog_round_16
            )
        )
        val title = mWaitingDialog?.findViewById<TextView>(R.id.tv_message)
        title?.text = getString(R.string.messageWaitingForSidekickStartWork)

        mWaitingDialog?.setOnDismissListener {
            if(mSidekickDetail?.isApproved == 0){

                findNavController().navigateUp()
            }

        }

    }


    private fun createConfirmWorkRateDialog() {
        mConfirmWorkRateDialog = Dialog(requireContext())
        mConfirmWorkRateDialog?.setContentView(R.layout.dialog_confirm_working_rate_user)
        mConfirmWorkRateDialog?.window?.setLayout(
            SizeConfig.screenWidth - 150,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        mConfirmWorkRateDialog?.window?.setBackgroundDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.bg_dialog_round_16
            )
        )
        val title = mConfirmWorkRateDialog?.findViewById<TextView>(R.id.tv_title)
        val progressBar = mConfirmWorkRateDialog?.findViewById<ProgressBar>(R.id.progress_bar)
        val groupBt = mConfirmWorkRateDialog?.findViewById<Group>(R.id.gp_buttons)
        val btCancelBooking = mConfirmWorkRateDialog?.findViewById<Button>(R.id.bt_cancel_booking)
        val btConfirm = mConfirmWorkRateDialog?.findViewById<Button>(R.id.bt_confirm)

        title?.text = "${getString(R.string.confirmWorkingRate)}\n$${mSidekickDetail?.newRate}"


        mConfirmWorkRateDialog?.setOnDismissListener {
            if(mSidekickDetail?.isApproved ==1){
                findNavController().navigateUp()
            }

        }


        btCancelBooking?.setSafeOnClickListener {
            mIBaseActivity?.hideKeyboard()

            hideConfirmWorkRateDialog()
            cancelBooking()
        }
        btConfirm?.setSafeOnClickListener {
            mIBaseActivity?.hideKeyboard()
            groupBt?.visibility = View.GONE
            progressBar?.visibility = View.VISIBLE

            confirmWorkingRates()
        }


    }


    private fun showWaitingDialog() {
        if (mWaitingDialog != null) {
            mWaitingDialog!!.show()
        } else {
            createWaitingDialog()
            showWaitingDialog()
        }
    }


    private fun hideWaitingDialog() {
        if (mWaitingDialog != null) {
            mWaitingDialog?.dismiss()
            mWaitingDialog = null
        }
    }


    private fun showConfirmWorkRateDialog() {
        if (mConfirmWorkRateDialog != null) {
            mConfirmWorkRateDialog!!.show()
        } else {
            createConfirmWorkRateDialog()
            showConfirmWorkRateDialog()
        }
    }


    private fun hideConfirmWorkRateDialog() {
        if (mConfirmWorkRateDialog != null) {
            mConfirmWorkRateDialog!!.dismiss()
            mConfirmWorkRateDialog = null
        }
    }


    override fun onStop() {
        mHandler.removeCallbacks(mRunnable)
        mIBaseActivity?.showProgressDialog(false)

        super.onStop()

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mIBaseActivity = context as IBaseActivity
    }

    override fun onDetach() {
        super.onDetach()
        mIBaseActivity = null
    }


    override fun onImageClick(images: List<String>, position: Int) {
        val slides = images.toTypedArray()
        val direction = NavGraphDirections.actionGlobalViewFullImageFragment(slides, position)
        findNavController().navigate(direction)
    }
}