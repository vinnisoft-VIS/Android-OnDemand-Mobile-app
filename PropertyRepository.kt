package com.vrsidekick.repository

import android.util.Log
import com.vrsidekick.models.*
import com.vrsidekick.network.RetrofitService
import com.vrsidekick.utils.Constants
import com.vrsidekick.viewModels.UploadPropertyData
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

class PropertyRepository(private val apiService: RetrofitService) : BaseRepository() {


    suspend fun addProperty(token: String, propertyData: UploadPropertyData): BaseResModel? {
        return try {
            safeApiCall(
                call = {
                    apiService.addPropertyAsync(
                        token,
                        Constants.HEADER_X_REQUESTED_WITH,
                        propertyData.propertyName!!.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        propertyData.propertyType!!.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        propertyData.latitude.toString()
                            .toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        propertyData.longitude.toString()
                            .toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        propertyData.description!!.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        propertyData.lotSize!!.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        propertyData.homeArea!!.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        propertyData.numFloors!!.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        propertyData.numBedrooms!!.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        propertyData.numBeds!!.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        propertyData.sleepsNum!!.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        propertyData.propertyOptions!!.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        propertyData.gatedCommunity!!.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        propertyData.iCalLink!!.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        propertyData.images!!,

                        ).await()
                },
                error = "Error from server"
            )

        } catch (e: Exception) {
            Log.d("TAG", "addProperty: Exception ${e.localizedMessage}")
            null
        }


    }


    suspend fun editProperty(token: String, propertyData: UploadPropertyData): BaseResModel? {
        return try {
            safeApiCall(
                call = {
                    apiService.editPropertyAsync(
                        token,
                        Constants.HEADER_X_REQUESTED_WITH,
                        propertyData.propertyId,
                        propertyData.propertyName!!.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        propertyData.propertyType!!.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        propertyData.latitude.toString()
                            .toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        propertyData.longitude.toString()
                            .toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        propertyData.description!!.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        propertyData.lotSize!!.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        propertyData.homeArea!!.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        propertyData.numFloors!!.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        propertyData.numBedrooms!!.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        propertyData.numBeds!!.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        propertyData.sleepsNum!!.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        propertyData.propertyOptions!!.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        propertyData.gatedCommunity!!.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        propertyData.iCalLink!!.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        propertyData.images!!,

                        ).await()
                },
                error = "Error from server"
            )

        } catch (e: Exception) {
            null
        }


    }


    suspend fun getMyProperties(token: String): MyPropertiesResModel? {

        return try {
            safeApiCall(
                call = { apiService.getMyPropertiesAsync(token,Constants.HEADER_X_REQUESTED_WITH,).await() },
                error = "Error from server"
            )

        } catch (e: Exception) {
            null
        }
    }


    suspend fun deleteProperty(token: String, propertyId: Long): BaseResModel? {

        return try {
            safeApiCall(
                call = { apiService.deletePropertyAsync(token,Constants.HEADER_X_REQUESTED_WITH, propertyId).await() },
                error = "Error from server"
            )

        } catch (e: Exception) {
            null
        }
    }


    suspend fun getPropertyDetail(
        token: String,
        propertyId: Long
    ): PropertyDetailResModel? {

        return try {
            safeApiCall(
                call = { apiService.getPropertyDetailAsync(token,Constants.HEADER_X_REQUESTED_WITH, propertyId).await() },
                error = "Error from server"
            )

        } catch (e: Exception) {
            Log.d("TAG", "getPropertyDetail: exception = ${e.localizedMessage}")
            null
        }
    }


    suspend fun getPropertySideKick(
        token: String,
        propertyId: Long
    ): PropertySidekickResModel? {

        return try {
            safeApiCall(
                call = { apiService.fetchPropertySideKickAsync(token,Constants.HEADER_X_REQUESTED_WITH, propertyId).await() },
                error = "Error from server"
            )

        } catch (e: Exception) {
            Log.d("TAG", "getPropertySidekick: exception = ${e.localizedMessage}")
            null
        }
    }

    suspend fun getSidekickDetail(
        token: String,
        bookingId: Long
    ): SidekickDetailResModel? {

        return try {
            safeApiCall(
                call = { apiService.fetchSidekickDetailAsync(token,Constants.HEADER_X_REQUESTED_WITH, bookingId).await() },
                error = "Error from server"
            )

        } catch (e: Exception) {
            Log.d("TAG", "getPropertySidekick: exception = ${e.localizedMessage}")
            null
        }
    }


    suspend fun cancelBooking(
        token: String,
        bookingId: Long
    ): BaseResModel? {

        return try {
            safeApiCall(
                call = { apiService.cancelBookingAsync(token, Constants.HEADER_X_REQUESTED_WITH,bookingId).await() },
                error = "Error from server"
            )

        } catch (e: Exception) {
            Log.d("TAG", "Cancel Booking: exception = ${e.localizedMessage}")
            null
        }
    }

    suspend fun confirmWorkingRates(
        token: String,
        bookingId: Long
    ): BaseResModel? {

        return try {
            safeApiCall(
                call = {
                    apiService.confirmWorkingRatesAsync(
                        token, Constants.HEADER_X_REQUESTED_WITH,
                        bookingId
                    ).await()
                },
                error = "Error from server"
            )

        } catch (e: Exception) {
            Log.d("TAG", "Cancel Booking: exception = ${e.localizedMessage}")
            null
        }
    }


    suspend fun getMySidekicks(
        token: String,
        propertyId: Long
    ): MySidekicksResModel? {

        return try {
            safeApiCall(
                call = {
                    apiService.fetchMySidekickAsync(
                        token,
                        Constants.HEADER_X_REQUESTED_WITH,
                        propertyId
                    ).await()
                },
                error = "Error from server"
            )

        } catch (e: Exception) {
            Log.d("TAG", "getMy sidekick: exception = ${e.localizedMessage}")
            null
        }
    }

    suspend fun getJobs(
        token: String,

        ): JobsResModel? {

        return try {
            safeApiCall(
                call = { apiService.fetchJobsAsync(token,Constants.HEADER_X_REQUESTED_WITH).await() },
                error = "Error from server"
            )

        } catch (e: Exception) {
            Log.d("TAG", "getMy sidekick: exception = ${e.localizedMessage}")
            null
        }
    }
}