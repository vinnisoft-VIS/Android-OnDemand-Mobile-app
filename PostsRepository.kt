package com.vrsidekick.repository

import android.content.ContentValues.TAG
import android.util.Log
import com.vrsidekick.models.*
import com.vrsidekick.network.RetrofitService
import com.vrsidekick.utils.Constants

class PostsRepository(private val apiService:RetrofitService) : BaseRepository() {



    suspend fun getServiceCategories(
        token:String
    ):CategoryResModel?{

        return try {
            safeApiCall(
                call = {apiService.getServiceCategoriesAsync(token, Constants.HEADER_X_REQUESTED_WITH).await()},
                error = "Error from server"
            )

        }catch (e:Exception){
            null
        }
    }


    suspend fun getProviderAds(
        token:String,
        categoryId:Long
    ):ProviderAdsResModel?{

        return try {
            safeApiCall(
                call = {apiService.getProvidersAdsAsync(token,"XMLHttpRequest",categoryId,).await()},
                error = "Error from server"
            )

        }catch (e:Exception){
            Log.d("TAG", "getProviderAds: exception = ${e.localizedMessage}")
            null
        }
    }



    suspend fun getPropertyAds(
        token:String,
        categoryId:Long
    ):PropertyAdsResModel?{

        return try {
            safeApiCall(
                call = {apiService.getPropertyAdsAsync(token,Constants.HEADER_X_REQUESTED_WITH,categoryId).await()},
                error = "Error from server"
            )

        }catch (e:Exception){
            Log.d(TAG, "getPropertyAds: propertyP=${e.localizedMessage}")
            null
        }
    }


    suspend fun addProperty(
        token:String,
        categoryId:Long,
        propertyId:Long,
    ):BaseResModel?{

        return try {
            safeApiCall(
                call = {apiService.addPostAsync(token,Constants.HEADER_X_REQUESTED_WITH,categoryId,propertyId).await()},
                error = "Error from server"
            )

        }catch (e:Exception){
            null
        }
    }




}