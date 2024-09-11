package com.example.sacrenachat.repository.networkrequest

import retrofit2.Response

interface NetworkRequestCallbacks {

    fun onSuccess(response: Response<*>)

    fun onError(t: Throwable)

}