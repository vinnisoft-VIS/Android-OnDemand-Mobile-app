package com.vrsidekick.repository

import com.vrsidekick.models.BaseResModel
import com.vrsidekick.models.CardsResModel
import com.vrsidekick.models.ProfileResModel
import com.vrsidekick.network.RetrofitService
import com.vrsidekick.utils.Constants
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class AccountRepository(private val apiService: RetrofitService) : BaseRepository() {


    suspend fun getProfile(token: String): ProfileResModel? {
        return try {
            safeApiCall(
                call = { apiService.getProfileAsync(token, Constants.HEADER_X_REQUESTED_WITH).await() },
                error = "Error from server"
            )
        } catch (e: Exception) {
            null
        }
    }


    suspend fun editUserProfile(
        token: String,
        name: String,
        address: String,
        image: MultipartBody.Part?
    ): ProfileResModel? {
        return try {
            safeApiCall(
                call = {
                    apiService.editProfileUserAsync(
                        token,
                        Constants.HEADER_X_REQUESTED_WITH,
                        name.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        image,
                        address.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        ).await()
                },
                error = "Error from server"
            )
        } catch (e: Exception) {
            null
        }
    }


    suspend fun editProviderProfile(
        token: String,
        name: String,
        image: MultipartBody.Part?
    ): ProfileResModel? {
        return try {
            safeApiCall(
                call = {
                    apiService.editProfileProviderAsync(
                        token,
                        Constants.HEADER_X_REQUESTED_WITH,
                        name.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        image,
                    ).await()
                },
                error = "Error from server"
            )
        } catch (e: Exception) {
            null
        }
    }








}