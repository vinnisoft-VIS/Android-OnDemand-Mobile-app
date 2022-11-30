package com.vrsidekick.repository

import com.vrsidekick.network.Output
import retrofit2.Response

open class BaseRepository {

    suspend fun <T : Any> safeApiCall(call: suspend () -> Response<T>, error: String): T? {

        val result = apiOutput(call, error)

        var output: T? = null

        when (result) {
            is Output.Success ->
                output = result.output


            is Output.Error -> result.statusCode
            /* Log.e("Error", "The $error and The ${result.exception}: ")*/

        }
        return output


    }


    private suspend fun <T : Any> apiOutput(
        call: suspend () -> Response<T>,
        error: String
    ): Output<T> {
        val response = call.invoke()
        return if (response.isSuccessful)
            Output.Success(response.body()!!)
        else
            Output.Error(response.code(),java.io.IOException("OOps Something went wrong due to $error"))
        // Output.Error(response.body())

    }
}