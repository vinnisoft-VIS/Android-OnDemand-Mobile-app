package com.vrsidekick.repository

import com.vrsidekick.models.BaseResModel
import com.vrsidekick.models.CardsResModel
import com.vrsidekick.network.RetrofitService

class ManagePaymentRepository(private val apiService:RetrofitService) : BaseRepository() {



    suspend fun addCard(
        token: String,
        xRequestWithToken:String,
        stripeToken:String
    ): BaseResModel? {
        return try {
            safeApiCall(
                call = {
                    apiService.addCardAsync(
                        token,
                        xRequestWithToken,
                        stripeToken
                    ).await()
                },
                error = "Error from server"
            )
        } catch (e: Exception) {
            null
        }
    }


    suspend fun deleteCard(
        token: String,
        xRequestWithToken:String,
        cardId:String
    ): BaseResModel? {
        return try {
            safeApiCall(
                call = {
                    apiService.deleteCardAsync(
                        token,
                        xRequestWithToken,
                        cardId
                    ).await()
                },
                error = "Error from server"
            )
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getCardsAsync(
        token: String,
        xRequestWithToken:String,

        ): CardsResModel? {
        return try {
            safeApiCall(
                call = {
                    apiService.getCardsAsync(
                        token,
                        xRequestWithToken,

                        ).await()
                },
                error = "Error from server"
            )
        } catch (e: Exception) {
            null
        }
    }


}