package com.vrsidekick.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.vrsidekick.R
import com.vrsidekick.activities.ProviderActivity
import com.vrsidekick.activities.prefs
import com.vrsidekick.fragments.provider.HomeFragmentP
import java.io.IOException
import java.util.*


private const val AUTOCOMPLETE_REQUEST_CODE = 101

class LocationHelper(private val activity: AppCompatActivity) {


    private var mFusedLocationProvideClient: FusedLocationProviderClient? = null
    var latLng = LatLng(0.0, 0.0)


    private var mCallback: ILocationHelper? = null
    private val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )


    init {
        mFusedLocationProvideClient = LocationServices.getFusedLocationProviderClient(activity)


    }


    private val locationPermissionResult =
        activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                getCurrentLocation()
            } else {
                Toast.makeText(activity, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    private val mLocationCallback: LocationCallback = object : LocationCallback() {

        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            val locations = locationResult.locations
            if (locations.isNotEmpty()) {
                mFusedLocationProvideClient?.removeLocationUpdates(this)
                latLng = LatLng(locations[0].latitude, locations[0].longitude)
                mCallback?.onLocationResult(latLng)
                prefs.currentLocation = latLng
            }
        }

        override fun onLocationAvailability(p0: LocationAvailability) {
            super.onLocationAvailability(p0)
        }
    }


    private val placesSearchResultHandler =
        (activity as AppCompatActivity).registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {

            if (it.resultCode == Activity.RESULT_OK) {
                val place: Place =
                    Autocomplete.getPlaceFromIntent(it?.data ?: return@registerForActivityResult)
                latLng = place.latLng ?: LatLng(0.0, 0.0)
                mCallback?.onLocationResult(latLng)
                prefs.currentLocation = latLng
                Log.d("TAG", "onActivityResult: ${place.latLng}    ${place.address}")


            } else if (it.resultCode == AutocompleteActivity.RESULT_ERROR) {
                val status: Status =
                    Autocomplete.getStatusFromIntent(it.data ?: return@registerForActivityResult)
                Log.d("TAG", "onActivityResult: ${status.statusMessage}")

            }

        }

    @SuppressLint("MissingPermission")
    fun getCurrentLocation() {
        Log.d("TAG", "getCurrentLocation: getCurrentLocation")
        if (hasLocationPermission()) {
            mFusedLocationProvideClient?.lastLocation?.addOnSuccessListener { location: Location? ->
                if (location == null) {
                    startLocationUpdate()
                } else {
                    latLng = LatLng(location.latitude, location.longitude)
                    mCallback?.onLocationResult(latLng)
                    prefs.currentLocation = latLng
                }
            }
        } else {

            locationPermissionResult.launch(Manifest.permission.ACCESS_FINE_LOCATION)


        }

    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdate() {
        mFusedLocationProvideClient?.requestLocationUpdates(
            createLocationRequest(),
            mLocationCallback,
            Looper.getMainLooper()
        )

    }


    private fun createLocationRequest(): LocationRequest {
        return LocationRequest.create().apply {
            interval = 10 * 1000
            fastestInterval = 5 * 1000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }


    private fun hasLocationPermission(): Boolean {
        locationPermissions.forEach {
            if (ContextCompat.checkSelfPermission(
                    activity,
                    it
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }


    fun searchPlaces() {
        val apiKey = activity.getString(R.string.api_key)
        if (!Places.isInitialized()) {
            Places.initialize(activity, apiKey)
        }
        val fields = listOf(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
            .setTypeFilter(TypeFilter.ESTABLISHMENT)
            .build(activity)
        placesSearchResultHandler.launch(intent)

    }


    fun getAddress(
        context: Context,
        latLng: LatLng?
    ): String {


        Log.d("TAG", "getAddress: ")




        try {

            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses: List<Address> =
                geocoder.getFromLocation(latLng!!.latitude, latLng.longitude, 1)
            if (addresses.isEmpty()) return context.getString(com.vrsidekick.R.string.noAddressAvailable)
            // val address = addresses[0]


            val address =
                addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

            val city = addresses[0].locality
            val state = addresses[0].adminArea
            //val country = addresses[0].countryName
            //val postalCode = addresses[0].postalCode
            //val knownName = addresses[0].featureName

            val tempAddress = address.split(",")



            Log.d("TAG", "getAddress: addressLine ${address}")
            Log.d("TAG", "getAddress: locality ${addresses[0].locality}")
            Log.d("TAG", "getAddress: adminArea ${addresses[0].adminArea}")
            Log.d("TAG", "getAddress: featureName ${addresses[0].featureName}")
            Log.d("TAG", "getAddress: subAdminArea ${addresses[0].subAdminArea}")
            Log.d("TAG", "getAddress: subLocality ${addresses[0].subLocality}")
            Log.d("TAG", "getAddress: thoroughfare ${addresses[0].thoroughfare}")
            Log.d("TAG", "getAddress: subThoroughfare ${addresses[0].subThoroughfare}")
            Log.d("TAG", "getAddress: premises ${addresses[0].premises}")


            /*val strReturnedAddress = StringBuilder("")

            for (i in 0..address.maxAddressLineIndex) {
                strReturnedAddress.append(address.getAddressLine(i)).append(" ")
            }*/
            // return strReturnedAddress.toString().trim()
            // return "$address,$city,$state,$country,$postalCode,$knownName"

            //  return "$city, $state"
            if (tempAddress.size >= 3) {
                return "${tempAddress[0]},${tempAddress[1]},${tempAddress[2]}"
            } else {
                return context.getString(R.string.noAddressAvailable)
            }

        } catch (e: IOException) {
            return context.getString(R.string.noAddressAvailable)

        }catch (e: NullPointerException) {
            return context.getString(R.string.noAddressAvailable)

        }
        catch (e: NumberFormatException) {
            return context.getString(R.string.noAddressAvailable)
        } catch (e: IllegalArgumentException) {
            return context.getString(R.string.noAddressAvailable)
        }


    }


    fun setOnLocationUpdateListener(callback: ILocationHelper) {
        mCallback = callback

    }


    interface ILocationHelper {
        fun onLocationResult(latLng: LatLng)
    }


}