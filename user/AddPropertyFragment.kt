package com.vrsidekick.fragments.user

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.model.LatLng
import com.vrsidekick.R
import com.vrsidekick.activities.BaseActivity
import com.vrsidekick.activities.prefs
import com.vrsidekick.adapter.provider.ServiceCatArrayAdapter
import com.vrsidekick.adapter.user.AddPropertyImgAdapter
import com.vrsidekick.databinding.FragmentAddPropertyBinding
import com.vrsidekick.dialogs.AddPropertyType
import com.vrsidekick.interfaces.IBaseActivity
import com.vrsidekick.models.Category
import com.vrsidekick.models.Property
import com.vrsidekick.models.PropertyDetail
import com.vrsidekick.utils.*
import com.vrsidekick.viewModels.PostsViewModel
import com.vrsidekick.viewModels.PropertyViewModel
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.concurrent.Executors


private const val TAG = "AddPropertyFragment"

class AddPropertyFragment : Fragment(), ImagePickerHelper.IImagePickerHelper,
    AddPropertyImgAdapter.IAddPropertyImages, LocationHelper.ILocationHelper {
    private lateinit var binding: FragmentAddPropertyBinding
    private val propertyViewModel: PropertyViewModel by activityViewModels()
    private val postsViewModel: PostsViewModel by viewModels()
    private val navArgs: AddPropertyFragmentArgs by navArgs()
    private var mAddPropertyImageAdapter: AddPropertyImgAdapter? = null
    private val propertyTypes = arrayOf("Residential", "Commercial", "Industrial")
    private var mPropertyTypesAdapter: ArrayAdapter<String>? = null
    private var mIBaseActivity: IBaseActivity? = null
    private var mServiceCategoryAdapter: ServiceCatArrayAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        (activity as BaseActivity).getImagePickerHelper().setOnImagePickedListener(this)
        (activity as BaseActivity).getLocationHelper().setOnLocationUpdateListener(this)
        mAddPropertyImageAdapter = AddPropertyImgAdapter()
        mAddPropertyImageAdapter?.setOnPropertyImageRemoveListener(this)
        mPropertyTypesAdapter = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_list_item_1,
            propertyTypes
        )
        mServiceCategoryAdapter =
            ServiceCatArrayAdapter(requireContext(), android.R.layout.simple_list_item_1)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddPropertyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLocation()
        propertyViewModel.isEditProperty = navArgs.isEditProperty
        (activity as BaseActivity).isShowBottomNavBar()
        (activity as BaseActivity).getImagePickerHelper().mRemainingImageSelection =
            mAddPropertyImageAdapter?.itemCount ?: 0

        //   setupPageTitle()
        binding.rvPropertyImages.setHasFixedSize(true)
        binding.rvPropertyImages.adapter = mAddPropertyImageAdapter
        binding.autocompleteEtPropertyType.setAdapter(mPropertyTypesAdapter)

        setupClickListener()
        setupObserver()
        setupObserver()



        getPropertyDetail()

    }


    private fun setupLocation() {

        binding.tvAddress.text =
            (activity as BaseActivity).getLocationHelper()
                .getAddress(requireContext(), prefs.currentLocation)
    }

    private fun getPropertyDetail() {
        if (navArgs.isEditProperty) {

            navArgs.property?.let {
                populateDataInViewModel(it)
            }

        }
    }


    private fun setupFormData() {
        if (propertyViewModel.isEditProperty) {
            val uploadPropertyData = propertyViewModel.getPropertyData()
            binding.etPropertyName.setText(uploadPropertyData.propertyName ?: "")
            binding.autocompleteEtPropertyType.setText(uploadPropertyData.propertyType ?: "")
            binding.etDescription.setText(uploadPropertyData.description ?: "")
            binding.etLotSize.setText(uploadPropertyData.lotSize)
            binding.etHomeArea.setText(uploadPropertyData.homeArea)
            binding.switchGatedCommunity.isChecked = uploadPropertyData.gatedCommunity == "1"
        }

    }

    private fun setupObserver() {
        propertyViewModel.getProgressObserver.observe(viewLifecycleOwner) {
            mIBaseActivity?.showProgressDialog(it)
        }
        propertyViewModel.getMessageObserver.observe(viewLifecycleOwner) {
            mIBaseActivity?.showMessage(it)

        }

        propertyViewModel.getPropertyImagesObserver.observe(viewLifecycleOwner) {
            mAddPropertyImageAdapter?.setData(it)
            (activity as BaseActivity).getImagePickerHelper().mRemainingImageSelection =
                mAddPropertyImageAdapter?.itemCount ?: 0

        }




    }

    private fun populateDataInViewModel(it: Property) {

        val uploadData = propertyViewModel.getPropertyData()
        uploadData.propertyId = navArgs.property?.id?:0
        uploadData.propertyName = it.name
        uploadData.propertyType = it.type
        try {
            uploadData.latitude =  it.latitude?.toDouble()
            uploadData.longitude = it.longitude?.toDouble()
        }catch (e:Exception){
            uploadData.latitude =  0.0
            uploadData.longitude = 0.0
        }

        uploadData.description = it.description
        uploadData.lotSize = it.lotSize
        uploadData.homeArea = it.homeSqFt
        uploadData.gatedCommunity = it.gatedCommunity.toString()
        uploadData.numFloors = it.floors
        uploadData.numBedrooms = it.bedrooms
        uploadData.numBeds = it.beds
        uploadData.sleepsNum = it.sleepsNumber
        uploadData.propertyOptions = it.option.toString()
        uploadData.iCalLink = it.icalLink

        val executer = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())

        Log.d(TAG, "populateDataInViewModel: ")
        (activity as BaseActivity).getLocationHelper().getAddress(
            requireContext(),
            LatLng(uploadData.latitude ?: 0.0, uploadData.longitude ?: 0.0)
        )


        if(propertyViewModel.propertyImages.isNotEmpty())return
        executer.execute {
            handler.post { binding.progressAllImages.visibility = View.VISIBLE }
            propertyViewModel.propertyImages.clear()
            it.images.forEach { image ->

                var bitmap: Bitmap? = null
                val inputStream: InputStream
                try {
                    inputStream = URL(image).openStream()
                    bitmap = BitmapFactory.decodeStream(inputStream)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                propertyViewModel.addImage(bitmap)
            }
            handler.post { binding.progressAllImages.visibility = View.GONE }
        }




      setupFormData()


    }

    private fun setupClickListener() {
        binding.ivClose.setSafeOnClickListener(1000) {
            requireActivity().viewModelStore.clear()
            it.findNavController().navigateUp()
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

        binding.gpLocation.setAllOnClickListener {
            /*  val direction = HomeFragmentDirections.actionNavHomeToSelectLocationFragment()
              findNavController().navigate(direction)*/
            (activity as BaseActivity).getLocationHelper().searchPlaces()
        }

        binding.btNext.setSafeOnClickListener(1000) {
            mIBaseActivity?.hideKeyboard()
            validateFormData()
        }
    }

    private fun validateFormData() {
        val propertyName = binding.etPropertyName.text.toString().trim()
        val propertyType = binding.autocompleteEtPropertyType.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()
        val lotSize = binding.etLotSize.textSize.toString().trim()
        val homeArea = binding.etHomeArea.text.toString().trim()
        val gatedCommunity = if (binding.switchGatedCommunity.isChecked) "1" else "0"



        if (propertyName.isEmpty()) {
            mIBaseActivity?.showMessage(getString(R.string.messageEnterPropertyName))
            return
        }


        if (mAddPropertyImageAdapter?.itemCount ?: 0 <= 0) {
            mIBaseActivity?.showMessage(getString(R.string.messageSelectPropertyImages))
            return
        }


        if (propertyType.isEmpty()) {
            mIBaseActivity?.showMessage(getString(R.string.messageSelectPropertyType))
            return
        }



        if (description.isEmpty()) {
            mIBaseActivity?.showMessage(getString(R.string.messageEnterDescription))
            return
        }



        if (lotSize.isEmpty()) {
            mIBaseActivity?.showMessage(getString(R.string.messageEnterLotSize))
            return
        }

        if (homeArea.isEmpty()) {
            mIBaseActivity?.showMessage(getString(R.string.messageEnterHomeArea))
            return
        }


        propertyViewModel.getPropertyData().propertyName = propertyName
        propertyViewModel.getPropertyData().propertyType = propertyType
        propertyViewModel.getPropertyData().lotSize = lotSize
        propertyViewModel.getPropertyData().homeArea = homeArea
        propertyViewModel.getPropertyData().gatedCommunity = gatedCommunity
        propertyViewModel.getPropertyData().description = description
        propertyViewModel.getPropertyData().latitude = prefs.currentLocation?.latitude
        propertyViewModel.getPropertyData().longitude = prefs.currentLocation?.longitude


        val direction =
            AddPropertyFragmentDirections.actionAddPropertyFragmentToAddProperty2Fragment()
        findNavController().navigate(direction)


    }

    override fun onImagePicked(image: Uri?) {
        image?.let {
            val inputStream = requireActivity().contentResolver.openInputStream(it)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            propertyViewModel.addImage(bitmap)
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

    override fun onRemove(index: Int) {
        propertyViewModel.removeImage(index)
    }

    override fun onLocationResult(latLng: LatLng) {


        try {
            Log.d(
                TAG,
                "onLocationResult: ${
                    (requireActivity() as BaseActivity).getLocationHelper()
                        .getAddress(requireContext(), latLng)
                }"
            )
            val locationName =
                (requireActivity() as BaseActivity).getLocationHelper()
                    .getAddress(requireContext(), latLng)
            binding.tvAddress.text = locationName
        } catch (e: Exception) {
            LogHelper.print("Location result exception=", e.localizedMessage)
        }


    }


}