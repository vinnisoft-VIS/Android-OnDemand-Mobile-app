package com.vrsidekick.fragments.user

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.vrsidekick.R
import com.vrsidekick.activities.BaseActivity
import com.vrsidekick.activities.UserActivity
import com.vrsidekick.activities.prefs
import com.vrsidekick.databinding.FragmentProfileBinding
import com.vrsidekick.interfaces.IBaseActivity
import com.vrsidekick.models.ProfileResModel
import com.vrsidekick.models.User
import com.vrsidekick.utils.ImagePickerHelper
import com.vrsidekick.utils.loadFromUrl
import com.vrsidekick.utils.setSafeOnClickListener
import com.vrsidekick.viewModels.AccountViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.util.*

private const val TAG = "ProfileFragment"

class ProfileFragment : Fragment(), ImagePickerHelper.IImagePickerHelper {
    private lateinit var binding: FragmentProfileBinding
    private val accountViewModel: AccountViewModel by viewModels()
    private var mIBaseActivity: IBaseActivity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as BaseActivity).getImagePickerHelper().setOnImagePickedListener(this)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as BaseActivity).isShowBottomNavBar()
        setupClickListener()
        setupAccountObserver()
        accountViewModel.getProfile("Bearer ${prefs.userToken}")
    }

    private fun setupAccountObserver() {
        accountViewModel.getProgressObserver.observe(viewLifecycleOwner) {
            mIBaseActivity?.showProgressDialog(it)

        }

        accountViewModel.getMessageObserver.observe(viewLifecycleOwner) {
            mIBaseActivity?.showMessage(it)

        }

        accountViewModel.getProfileObserver.observe(viewLifecycleOwner) {
            prefs.currentUser = it
            setupUiData(it)

        }

        accountViewModel.getEditProfileObserver.observe(viewLifecycleOwner) {
            prefs.currentUser = it.user
            setupUiData(it.user)
            MaterialAlertDialogBuilder(requireActivity())
                .setTitle(getString(R.string.success))
                .setMessage(it.message)
                .setPositiveButton(getString(R.string.ok)){dialog,which ->
                    dialog.dismiss()
                    binding.ivBack.callOnClick()
                }
                .show()

        }
    }

    private fun setupUiData(it: User?) {
        it?.let { user ->
            binding.btVerifyPhone.visibility =
                if (it.loginType?.lowercase() != "manual") View.VISIBLE else View.GONE
            binding.ivUser.loadFromUrl(user.profilePhotoUrl)
            binding.etName.setText(user.name)
            binding.etEmail.setText(user.email)
            binding.etPhone.setText(user.phoneNumber)
            binding.etAddress.setText(user.address)

        }

    }


    private fun setupClickListener() {
        binding.ivBack.setSafeOnClickListener(1000) {
            it.findNavController().navigateUp()
        }

        binding.ivEditImage.setOnClickListener {
            mIBaseActivity?.hideKeyboard()
            (requireActivity() as BaseActivity).getImagePickerHelper()
                .openImagePickOptionsDialog()

        }


        binding.btSave.setSafeOnClickListener {
            validateProfileForm()
        }
    }

    private fun validateProfileForm() {
        mIBaseActivity?.hideKeyboard()
        val name = binding.etName.text.toString().trim()
        val address = binding.etAddress.text.toString().trim()

        if (name.isEmpty()) {
            mIBaseActivity?.showMessage(getString(R.string.messageNameRequired))
            return
        }

        if (address.isEmpty()) {
            mIBaseActivity?.showMessage(getString(R.string.messageEnterAddressDetails))
            return
        }

        val imagePart = prepareImagePart()

        accountViewModel.editUserProfile(
            "Bearer ${prefs.userToken}",
            name, address, imagePart
        )

    }

    private fun prepareImagePart(): MultipartBody.Part? {
        return try {
            val bitmap = (binding.ivUser.drawable as BitmapDrawable).bitmap
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            val byteArray = outputStream.toByteArray()
            val requestBody =
                byteArray.toRequestBody("file/*".toMediaTypeOrNull(), 0, byteArray.size)
            MultipartBody.Part.createFormData(
                "image",
                "user_profile${Random().nextInt(1000)}.jpg",
                requestBody
            )

        } catch (e: Exception) {
            null
        }

    }


    override fun onImagePicked(image: Uri?) {
        image?.let {
            Glide.with(binding.ivUser)
                .load(it)
                .placeholder(R.drawable.img_placeholder_user)
                .error(R.drawable.img_placeholder_user)
                .into(binding.ivUser)


        }


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
}