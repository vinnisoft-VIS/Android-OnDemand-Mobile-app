package com.vrsidekick.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Color
import android.location.Address
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.vrsidekick.network.ApiFactory
import com.google.android.material.snackbar.Snackbar
import java.util.*
import android.location.Geocoder
import android.util.Log
import java.text.SimpleDateFormat


object Global {




    fun showMessage(rootView: View, message: String, length: Int = Snackbar.LENGTH_SHORT) {
        val sb = Snackbar.make(rootView, message, length)
        sb.setBackgroundTint(Color.BLACK)
        sb.show()

    }

    fun getCompleteUrl(source: String?): String {
        if (source == null) return ""
        return if (source.startsWith("http")) {
            source
        }  else {
            ApiFactory.IMAGE_BASE_URL + source
        }
    }


    //2022-05-05T08:48:20.000000Z
     fun formatDate(source:String?) : String? {
        return  try {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss", Locale.getDefault())
            val date = sdf.parse(source)
            return  SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(date)
        }catch (e:Exception){
            null
        }


    }


    fun showSoftKeyboard(context: Context,view:View){
        if(view.requestFocus()){

            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view,InputMethodManager.SHOW_IMPLICIT)
        }
    }










    fun requiredRational(activity: Activity?, permissions: String?): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity!!, permissions!!)
    }

    fun getRealPathFromURI(context: Context, contentUri: Uri?): String? {
        if (contentUri == null) return ""
        var path: String? = null
        val proj = arrayOf(MediaStore.MediaColumns.DATA)
        val cursor: Cursor? = context.getContentResolver().query(contentUri, proj, null, null, null)
        cursor?.let {
            if (it.moveToFirst()) {
                val column_index: Int = it.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
                path = it.getString(column_index)
            }
            cursor.close()
        }

        return path
    }


    fun hideKeyboard(context: Context, view: View) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun hasInternet(context: Context): Boolean {

        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val activeNetworks = connectivityManager.activeNetwork ?: return false
        val activeNetworkCapabilities =
            connectivityManager.getNetworkCapabilities(activeNetworks) ?: return false
        return when {
            activeNetworkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            activeNetworkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetworkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            activeNetworkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
            else -> false

        }

    }


    fun getAddress(context: Context, latitude: Double?, longitude: Double?): String {
        Log.d("TAG", "getAddress: latitude = $latitude and longitude = $longitude")
        if(latitude==null ||longitude==null)return "Address unavailable"
        try {
            val addresses: List<Address>
            val geocoder = Geocoder(context, Locale.getDefault())

            addresses = geocoder.getFromLocation(
                latitude,
                longitude,
                1
            ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5


            Log.d("TAG", "getAddress: ADDRESS  =$addresses")

            Log.d("TAG", "getAddress: city =${addresses[0].locality}")
            Log.d("TAG", "getAddress: state =${addresses[0].adminArea}")
            Log.d("TAG", "getAddress: country =${addresses[0].countryName}")
            Log.d("TAG", "getAddress: postalCode =${addresses[0].postalCode}")
            Log.d("TAG", "getAddress: knownName =${addresses[0].featureName}")
            Log.d("TAG", "getAddress: knownName =${addresses[0].subAdminArea}")
            Log.d("TAG", "getAddress: knownName =${addresses[0].subLocality}")

            val _address = addresses[0]
            //val _locality = if (_address.subLocality ==null) _address.locality else _address.subLocality
            val _locality = _address.locality?:"Unnamed area"
            //val _adminArea = if (_address.subAdminArea ==null) _address.adminArea else _address.subAdminArea
            val _adminArea = _address.adminArea
            return "$_locality,$_adminArea"

        }catch (e:Exception){
          return  ""
        }


    }

    fun hasFeatureCamera(context: Context): Boolean {
        val pm = context.packageManager
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
    }

    fun hasPermissions(context: Context, permissions: Array<String>): Boolean {
        var allGranted = false

        for (permission in permissions) {
            allGranted = (ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED)

        }

        return allGranted
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun hasInternetConnectivity(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        capabilities?.let {
            if (it.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                it.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
            )
                return true

        }
        return false
    }


    fun getGooglePlaceImageUrl(placeImgReference:String):String{
        return "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photo_reference=$placeImgReference&key=${Constants.API_KEY}"

    }


    fun openLink(context:Context,url:String){
        Intent(Intent.ACTION_VIEW,Uri.parse(url)).also {
            context.startActivity(it)
        }

    }


}