package com.vrsidekick.utils

import android.Manifest
import android.net.Uri
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.nguyenhoanglam.imagepicker.model.GridCount
import com.nguyenhoanglam.imagepicker.model.ImagePickerConfig
import com.nguyenhoanglam.imagepicker.model.RootDirectory
import com.nguyenhoanglam.imagepicker.ui.imagepicker.registerImagePicker
import com.vrsidekick.R
import kotlin.math.acos

class ImagePickerHelper(private val activity: AppCompatActivity) {

    private var mCallback: IImagePickerHelper? = null


    private var mMaxImageSelection: Int = 6

    var mRemainingImageSelection: Int = mMaxImageSelection
        set(value) {
            field = mMaxImageSelection - value
        }


    private val cropImageResult = activity.registerForActivityResult(CropImageContract()) {
        if (it.isSuccessful) {
            val uri = it.uriContent
            mCallback?.onImagePicked(uri)
        }
    }


    private val cameraPermissionResult =
        activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) {

            if (it) {
                startCrop()
            } else {
                Toast.makeText(activity, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }

        }


    private val multiImagePickerResult = activity.registerImagePicker { images ->
        // Selected images are ready to use
        for (imageData in images) {
            mCallback?.onImagePicked(imageData.uri)

        }
    }


    fun openImagePickOptionsDialog() {
        MaterialAlertDialogBuilder(activity)
            .setTitle(activity.getString(R.string.chooseOptions))
            .setItems(arrayOf("Camera", "Gallery")) { dialog, which ->
                if (which == 0) {
                    captureImage()
                } else {
                    openGallery()
                }
                dialog.dismiss()
            }.show()
    }


    private fun captureImage() {
        if (Global.hasPermissions(activity, arrayOf(Manifest.permission.CAMERA))) {
            startCrop()
        } else {
            cameraPermissionResult.launch(Manifest.permission.CAMERA)
        }

    }

    private fun startCrop() {
        cropImageResult.launch(
            options {
                setGuidelines(CropImageView.Guidelines.OFF)
                setImageSource(
                    includeCamera = true, includeGallery = false
                )
            }
        )
    }


    private fun openGallery() {
        val config = ImagePickerConfig(
            isFolderMode = true,
            folderTitle = "Album",
            maxSize = mRemainingImageSelection,
            isMultipleMode = true,
            rootDirectory = RootDirectory.PICTURES,
            subDirectory = "Photos",
            folderGridCount = GridCount(2, 4),
            imageGridCount = GridCount(3, 5),
        )
        multiImagePickerResult.launch(config)

    }


    fun setOnImagePickedListener(callback: IImagePickerHelper) {
        mCallback = callback
    }


    interface IImagePickerHelper {
        fun onImagePicked(image: Uri?)
    }


}