package com.vrsidekick.repository

import android.util.Log
import com.vrsidekick.models.BaseResModel
import com.vrsidekick.models.LoginResModel
import com.vrsidekick.models.SendOtpResModel
import com.vrsidekick.network.RetrofitService
import com.vrsidekick.utils.AccountType
import com.vrsidekick.utils.Constants

class AuthRepository(private val apiService: RetrofitService) : BaseRepository() {


    suspend fun socialLogin(
        name: String,
        email: String,
        socialId: String,
        socialAvatar: String,
        socialProvider: String,
        deviceType: String = "Android",
    ): LoginResModel? {
        return try {
            safeApiCall(
                call = {
                    apiService.socialLoginAsync(
                        name,
                        email,
                        socialId,
                        socialAvatar,
                        socialProvider,
                        deviceType
                    ).await()
                },
                error = "Error from server"
            )

        } catch (e: Exception) {
            null
        }
    }


    suspend fun registerUser(
        name: String,
        email: String,
        password: String,
        phoneCode: String,
        phoneNumber: String
    ): LoginResModel? {

        return try {
            safeApiCall(
                call = {
                    apiService.registerUserAsync(name, email, password, phoneCode, phoneNumber)
                        .await()
                },
                error = "Error from server"
            )

        } catch (e: Exception) {
            null
        }


    }

    suspend fun sendOtp(
        phoneCode: String,
        phoneNumber: String
    ): SendOtpResModel? {

        return try {
            safeApiCall(
                call = { apiService.sendOtpAsync(phoneCode, phoneNumber).await() },
                error = "Error from server"
            )

        } catch (e: Exception) {
            null
        }
    }


    suspend fun loginUser(
        email: String,
        password: String
    ): LoginResModel? {

        return try {
            safeApiCall(
                call = { apiService.loginUserAsync(email, password).await() },
                error = "Error from server"
            )

        } catch (e: Exception) {
            null
        }
    }


    suspend fun forgotPassword(
        email: String,
        ): BaseResModel? {

        return try {
            safeApiCall(
                call = { apiService.forgotPasswordAsync(email).await() },
                error = "Error from server"
            )

        } catch (e: Exception) {
            null
        }
    }

    suspend fun logoutUser(
        token: String,
    ): BaseResModel? {

        return try {
            safeApiCall(
                call = { apiService.logoutUserAsync(token, Constants.HEADER_X_REQUESTED_WITH).await() },
                error = "Error from server"
            )

        } catch (e: Exception) {
            Log.d("TAG", "logoutUser: exception = ${e.localizedMessage}")
            null
        }
    }



    suspend fun setUserType(
        token:String,
        accountType:AccountType
    ):BaseResModel?{

      return  try {
            safeApiCall(
                call = {apiService.setAccountTypeAsync(token,Constants.HEADER_X_REQUESTED_WITH,accountType.name.lowercase()).await()},
                error = "Error from server"
            )

        }catch (e:Exception){
            null
        }

    }


    suspend fun changePassword(
        token:String,
        currentPassword:String,
        newPassword:String,
        confirmPassword:String

    ): BaseResModel?{

      return  try {

          safeApiCall(
              call = {apiService.changePasswordAsync(token,Constants.HEADER_X_REQUESTED_WITH, currentPassword, newPassword, confirmPassword).await()},
              error = "Error from server"
          )
        }catch (e:Exception){
            null
        }
    }
}


