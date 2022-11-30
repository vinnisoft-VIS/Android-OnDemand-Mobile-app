package com.vrsidekick.fragments.provider

import android.app.Dialog
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.vrsidekick.NavGraphDirections
import com.vrsidekick.NavGraphProviderDirections
import com.vrsidekick.R
import com.vrsidekick.activities.BaseActivity
import com.vrsidekick.activities.prefs
import com.vrsidekick.adapter.user.AddPropertyImgAdapter
import com.vrsidekick.databinding.FragmentBookingDetailPBinding
import com.vrsidekick.interfaces.IBaseActivity
import com.vrsidekick.models.ProviderBookingDetail
import com.vrsidekick.utils.*
import com.vrsidekick.viewModels.ProviderViewModel

private const val TAG = "BookingDetailFragmentP"
private const val API_EXECUTION_DELAY = 5 * 1000L

class BookingDetailFragmentP : Fragment(), ImagePickerHelper.IImagePickerHelper,
    AddPropertyImgAdapter.IAddPropertyImages {
    private lateinit var binding: FragmentBookingDetailPBinding
    private val providerViewModel: ProviderViewModel by viewModels()
    private val navArgs: BookingDetailFragmentPArgs by navArgs()
    private var mIBaseActivity: IBaseActivity? = null
    private var mBookingDetail: ProviderBookingDetail? = null
    private var mAddPropertyImageAdapter: AddPropertyImgAdapter? = null
    private var mWaitingDialog: Dialog? = null
    private var mSetWorkingRateDialog: Dialog? = null
    private val mHandler = Handler(Looper.getMainLooper())
    private val mRunnable = object : Runnable {
        override fun run() {
            getBookingDetail()
            mHandler.postDelayed(this, API_EXECUTION_DELAY)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as BaseActivity).getImagePickerHelper().setOnImagePickedListener(this)
        mAddPropertyImageAdapter = AddPropertyImgAdapter()
        mAddPropertyImageAdapter?.setOnPropertyImageRemoveListener(this)
        createWaitingDialog()
        createSetWorkingRateDialog()


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBookingDetailPBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as BaseActivity).isShowBottomNavBar()
        binding.rvPropertyImages.setHasFixedSize(true)
        binding.rvPropertyImages.adapter = mAddPropertyImageAdapter
        getBookingDetail()

        setupClickListener()
        setupObserver()

    }

    override fun onStart() {
        super.onStart()
        mHandler.postDelayed(mRunnable, API_EXECUTION_DELAY)
    }

    override fun onStop() {
        mHandler.removeCallbacks(mRunnable)
        super.onStop()

    }

    private fun getBookingDetail() {
        providerViewModel.getProviderBookingDetails(
            "Bearer ${prefs.userToken}",
            navArgs.bookingId
        )
    }

    private fun setupObserver() {
        providerViewModel.getProgressObserver.observe(viewLifecycleOwner) {
            if (mBookingDetail != null) {
                mIBaseActivity?.showProgressDialog(false)
            } else {
                mIBaseActivity?.showProgressDialog(it)
            }

        }
        providerViewModel.getMessageObserver.observe(viewLifecycleOwner) {
            mIBaseActivity?.showMessage(it)
        }

        providerViewModel.getPropertyImagesObserver.observe(viewLifecycleOwner) {
            mAddPropertyImageAdapter?.setData(it)
            (activity as BaseActivity).getImagePickerHelper().mRemainingImageSelection =
                mAddPropertyImageAdapter?.itemCount ?: 0

        }

        providerViewModel.getProviderBookingDetailObserver.observe(viewLifecycleOwner) {
            setupUi(it)
        }

        providerViewModel.getPropertyWorkDoneObserver.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        providerViewModel.getSetWorkRateObserver.observe(viewLifecycleOwner) {
            hideSetWorkingRateDialog()
            getBookingDetail()
        }

    }

    private fun setupUi(bookingDetail: ProviderBookingDetail?) {
        mBookingDetail = bookingDetail

        bookingDetail?.let {
            if (it.isApproved == 2) {
                mHandler.removeCallbacks(mRunnable)
            }
            binding.tvPropertyName.text = it.propertyName
            binding.tvDate.text = Global.formatDate(it.createdAt)
            binding.tvTime.text = it.time
            binding.tvScheduleType.text = it.rateType
            binding.tvPrice.text = it.rate
            binding.btWorkDone.isEnabled = it.status == 0
            binding.gpImagesSetup.visibility = if (it.status == 0) View.VISIBLE else View.GONE
            binding.btWorkDone.text =
                if (it.isApproved == 0) getString(R.string.startWork) else getString(R.string.workDone)


            if(it.status ==0){
                if (it.isApproved == 1) {
                    showWaitingDialog()
                } else {
                    hideWaitingDialog()
                }

            }else{
                hideWaitingDialog()
            }



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
            mWaitingDialog!!.dismiss()
            mWaitingDialog = null
        }
    }


    private fun showSetWorkingRateDialog() {
        if (mSetWorkingRateDialog != null) {
            mSetWorkingRateDialog!!.show()
        } else {
            createSetWorkingRateDialog()
            showSetWorkingRateDialog()
        }
    }


    private fun hideSetWorkingRateDialog() {
        if (mSetWorkingRateDialog != null) {
            mSetWorkingRateDialog!!.dismiss()
            mSetWorkingRateDialog = null
        }
    }

    private fun setupClickListener() {
        binding.ivBack.setSafeOnClickListener(1000) {
            it.findNavController().navigateUp()
        }

        binding.btChat.setSafeOnClickListener {

            val direction = NavGraphDirections.actionGlobalChatFragment()
                findNavController().navigate(direction)


        }


        binding.btWorkDone.setSafeOnClickListener {

            mBookingDetail?.let { bookingDetails ->
                if (bookingDetails.isApproved == 0) {
                    showSetWorkingRateDialog()
                } else {
                    providerViewModel.propertyWorkDone(
                        "Bearer ${prefs.userToken}",
                        navArgs.bookingId
                    )
                }

            }

        }



        binding.gpAddImage.setAllOnClickListener {

            mIBaseActivity?.hideKeyboard()
            if ((activity as BaseActivity).getImagePickerHelper().mRemainingImageSelection == 0) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.maxImageReached),
                    Toast.LENGTH_SHORT
                ).show()

            } else {
                (requireActivity() as BaseActivity).getImagePickerHelper()
                    .openImagePickOptionsDialog()
            }

        }


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
        mWaitingDialog?.setOnDismissListener {
            if(mBookingDetail?.isApproved ==1){
                findNavController().navigateUp()
            }

        }

    }

    private fun createSetWorkingRateDialog() {
        mSetWorkingRateDialog = Dialog(requireContext())
        mSetWorkingRateDialog!!.setContentView(R.layout.dialog_set_working_rate_provider)
        mSetWorkingRateDialog!!.window?.setLayout(
            SizeConfig.screenWidth - 150,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        mSetWorkingRateDialog!!.window?.setBackgroundDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.bg_dialog_round_16
            )
        )

        val etWorkingPrice = mSetWorkingRateDialog?.findViewById<TextView>(R.id.et_working_price)
        val btConfirm = mSetWorkingRateDialog?.findViewById<Button>(R.id.bt_confirm)

        btConfirm?.setSafeOnClickListener {
            mIBaseActivity?.hideKeyboard()
            val workingPrice = etWorkingPrice?.text.toString().trim()
            if (workingPrice.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.messageWorkingRateRequired),
                    Toast.LENGTH_SHORT
                ).show()
                return@setSafeOnClickListener
            }

            providerViewModel.setWorkStartRate(
                "Bearer ${prefs.userToken}",
                mBookingDetail?.id ?: return@setSafeOnClickListener,
                workingPrice
            )


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

    override fun onImagePicked(image: Uri?) {
        image?.let {
            val inputStream = requireActivity().contentResolver.openInputStream(it)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            providerViewModel.addImage(bitmap)
        }


    }

    override fun onRemove(index: Int) {
        providerViewModel.removeImage(index)
    }
}