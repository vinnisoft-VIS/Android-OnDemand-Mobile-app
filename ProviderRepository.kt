package com.vrsidekick.repository

import android.util.Log
import com.vrsidekick.models.*
import com.vrsidekick.network.RetrofitService
import com.vrsidekick.utils.Constants
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class ProviderRepository(private val apiService: RetrofitService) : BaseRepository() {

    suspend fun addProviderService(
        token: String,
        latitude: Double,
        longitude: Double,
        serviceInMiles: Int,
        skill: String,
        categoryId: Long,
        monthlyRate: String,
        hourlyRate: String,
        tools: Int,
        supplies: Int,
        timeFrom: ArrayList<String>,
        timeTo: ArrayList<String>,
        days: ArrayList<Int>,

        ): BaseResModel? {

        return try {
            safeApiCall(
                call = {
                    apiService.addProviderServiceAsync(
                        token,
                        Constants.HEADER_X_REQUESTED_WITH,
                        latitude,
                        longitude,
                        serviceInMiles,
                        skill,
                        categoryId,
                        monthlyRate,
                        hourlyRate,
                        tools,
                        supplies,
                        timeFrom,
                        timeTo,
                        days

                    ).await()
                },
                error = "Error from server"
            )

        } catch (e: Exception) {
            null
        }

    }


    suspend fun toggleProviderFav(
        token: String,
        favUserId: Long?,
        categoryId: Long?,
    ): ToggleFavProviderResModel? {

        return try {
            safeApiCall(
                call = { apiService.toggleProviderFavAsync(token,
                    Constants.HEADER_X_REQUESTED_WITH,
                    favUserId, categoryId).await() },
                error = "Error from server"
            )

        } catch (e: Exception) {
             null

        }
    }


    suspend fun filterProvider(
        token: String,
        categoryId: Long,
        startTime: String,
        endTime: String,
        distance: Int,
        latitude: Double,
        longitude: Double,
        requiresTools: String,
        requiresSupplies: String,
        serviceType: String,
        minPrice: String,
        maxPrice: String,
        rating: String,
    ): ProviderAdsResModel? {
        return try {
            safeApiCall(
                call = {
                    apiService.filterProvidersAsync(
                        token,
                        Constants.HEADER_X_REQUESTED_WITH,
                        categoryId,
                        startTime,
                        endTime,
                        0,
                        latitude,
                        longitude,
                        requiresTools,
                        requiresSupplies,
                        serviceType,
                        minPrice,
                        maxPrice,
                        rating
                    ).await()
                },
                error = "Error from server"
            )

        } catch (e: Exception) {
            null
        }
    }


    suspend fun searchProvider(
        token: String,
        name: String,

        ): ProviderAdsResModel? {
        return try {
            safeApiCall(
                call = {
                    apiService.searchProviderAsync(
                        token,
                        Constants.HEADER_X_REQUESTED_WITH,
                        name,
                    ).await()
                },
                error = "Error from server"
            )

        } catch (e: Exception) {
            null
        }
    }


    suspend fun getProviderDetail(
        token: String,
        providerId: Long,
        categoryId: Long

    ): ProviderDetailResModel? {
        return try {
            safeApiCall(
                call = {
                    apiService.fetchProviderDetailAsync(
                        token,
                        Constants.HEADER_X_REQUESTED_WITH,
                        providerId,
                        categoryId
                    ).await()
                },
                error = "Error from server"
            )

        } catch (e: Exception) {
            Log.d("TAG", "getProviderDetail: exception = ${e.localizedMessage} ")
            null
        }
    }
suspend fun getReviewsUser(
        token: String,
        providerId: Long,


    ): ReviewsResModel? {
        return try {
            safeApiCall(
                call = {
                    apiService.fetchReviewsUserAsync(
                        token,
                        Constants.HEADER_X_REQUESTED_WITH,
                        providerId,

                    ).await()
                },
                error = "Error from server"
            )

        } catch (e: Exception) {
            null
        }
    }


    suspend fun getProviderAvailability(
        token: String,
        providerId: Long,
        categoryId: Long,
        availableDate:String

    ): ProviderAvailabilityResModel? {
        return try {
            safeApiCall(
                call = {
                    apiService.fetchProviderAvailabilityAsync(
                        token,
                        Constants.HEADER_X_REQUESTED_WITH,
                        providerId,
                        categoryId,
                        availableDate
                    ).await()
                },
                error = "Error from server"
            )

        } catch (e: Exception) {
            Log.d("TAG", "getProviderDetail: exception = ${e.localizedMessage} ")
            null
        }
    }


    suspend fun bookProvider(
        token: String,
        provider_id: Long,
        propertyId: Long,
        day: String,
        time: String,
        rateType: String,
        rate: String,
        supportFee: String,
        categoryId: Long,

        ): BaseResModel? {
        return try {
            safeApiCall(
                call = {
                    apiService.bookProviderAsync(
                        token,
                        Constants.HEADER_X_REQUESTED_WITH,
                        provider_id,
                        propertyId,
                        day,
                        time,
                        rateType,
                        rate,
                        supportFee,
                        categoryId
                    ).await()
                },
                error = "Error from server"
            )

        } catch (e: Exception) {
            Log.d("TAG", "getProviderDetail: exception = ${e.localizedMessage} ")
            null
        }
    }


    suspend fun getFavProvider(
        token: String,
    ): ProviderAdsResModel? {
        return try {
            safeApiCall(
                call = {
                    apiService.fetchFavouriteProviderAsync(
                        token,
                        Constants.HEADER_X_REQUESTED_WITH,
                    ).await()
                },
                error = "Error from server"
            )

        } catch (e: Exception) {
            Log.d("TAG", "fetch fav providers: exception = ${e.localizedMessage} ")
            null
        }
    }


    suspend fun getProviderBookings(
        token: String,
    ): ProviderBookingsResModel? {
        return try {
            safeApiCall(
                call = {
                    apiService.fetchProviderBookingsAsync(
                        token,
                        Constants.HEADER_X_REQUESTED_WITH,
                    ).await()
                },
                error = "Error from server"
            )

        } catch (e: Exception) {
            Log.d("TAG", "fetch fav providers: exception = ${e.localizedMessage} ")
            null
        }
    }


    suspend fun getProviderBookingDetails(
        token: String,
        bookingId: Long
    ): ProviderBookingDetailResModel? {
        return try {
            safeApiCall(
                call = {
                    apiService.fetchProviderBookingDetailAsync(
                        token,
                        Constants.HEADER_X_REQUESTED_WITH,
                        bookingId
                    ).await()
                },
                error = "Error from server"
            )

        } catch (e: Exception) {
            Log.d("TAG", "fetch fav providers: exception = ${e.localizedMessage} ")
            null
        }
    }


    suspend fun propertyWorkDone(
        token: String,
        bookingId: Long,
        images: ArrayList<MultipartBody.Part>?
    ): BaseResModel? {
        return try {
            safeApiCall(
                call = {
                    apiService.propertyWorkDoneAsync(
                        token,
                        Constants.HEADER_X_REQUESTED_WITH,
                        bookingId.toString()
                            .toRequestBody("multipart/form-data".toMediaTypeOrNull()),
                        images
                    ).await()
                },
                error = "Error from server"
            )

        } catch (e: Exception) {
            Log.d("TAG", "propertyWorkDone exception = ${e.localizedMessage} ")
            null
        }
    }



    suspend fun setWorkStartRates(
        token: String,
        bookingId: Long,
        rate:String
    ): BaseResModel? {
        return try {
            safeApiCall(
                call = {
                    apiService.setWorkStartRateAsync(
                        token,
                        Constants.HEADER_X_REQUESTED_WITH,
                        bookingId,
                        rate
                    ).await()
                },
                error = "Error from server"
            )

        } catch (e: Exception) {
            Log.d("TAG", "propertyWorkDone exception = ${e.localizedMessage} ")
            null
        }
    }






}