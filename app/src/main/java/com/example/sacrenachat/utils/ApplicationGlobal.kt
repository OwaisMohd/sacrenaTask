package com.example.sacrenachat.utils

import android.app.Application

class ApplicationGlobal : Application() {

    companion object {
        var accessToken: String = ""
        var deviceLocale: Int = 2
    }

    override fun onCreate() {
        super.onCreate()
    }
}