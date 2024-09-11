package com.example.sacrenachat.repository.networkrequest

import com.example.sacrenachat.R
import com.example.sacrenachat.repository.models.Error
import com.example.sacrenachat.repository.models.PojoBackendResponse
import com.example.sacrenachat.repository.models.PojoNetworkResponse
import okhttp3.ResponseBody
import java.io.IOException

object RetrofitRequest {

    fun checkForNetworkResponseCode(code: Int): PojoNetworkResponse {
        return when (code) {
            200 -> PojoNetworkResponse(true, false)
            403, 401 -> PojoNetworkResponse(false, true)
            else -> PojoNetworkResponse(false, false)
        }
    }

    fun checkForBackendResponseCode(code: Int): PojoBackendResponse {
        return when (code) {
            100 -> PojoBackendResponse(true, false, false)
            401 -> PojoBackendResponse(false, true, false)
            else -> PojoBackendResponse(false, false, true)
        }
    }

    fun getErrorMessage(responseBody: ResponseBody): String {
        val errorMessage = ""
        try {
            val errorConverter = RestClient.retrofitInstance!!
                .responseBodyConverter<Error>(Error::class.java, arrayOfNulls<Annotation>(0))
            return errorConverter.convert(responseBody)!!.message
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return errorMessage
    }

    fun getRetrofitError(t: Throwable): Int {
        return if (t is IOException) {
            R.string.no_internet
        } else {
            R.string.retrofit_failure
        }
    }

}