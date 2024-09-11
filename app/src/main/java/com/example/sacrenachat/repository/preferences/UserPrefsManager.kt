package com.example.sacrenachat.repository.preferences

import android.content.Context
import android.content.SharedPreferences
import com.example.sacrenachat.repository.models.UserProfile
import com.example.sacrenachat.utils.ApplicationGlobal
import com.google.gson.Gson

class UserPrefsManager(context: Context) {

    private val mSharedPreferences: SharedPreferences
    private val mEditor: SharedPreferences.Editor

    companion object {
        // SharedPreference Keys
        private const val PREFS_FILENAME = "Base App"
        private const val PREFS_MODE = 0
        private const val PREFS_USER_PROFILE = "userProfile"
        private const val PREFS_REACTION_LIST = "reactionList"
        private const val PREFS_IS_LOGINED = "isLogined"
        private const val PREFS_ACCESS_TOKEN = "accessToken"
    }

    init {
        mSharedPreferences = context.getSharedPreferences(PREFS_FILENAME, PREFS_MODE)
        mEditor = mSharedPreferences.edit()
    }

    fun clearUserPrefs() {
        ApplicationGlobal.accessToken = ""
        mEditor.clear()
        mEditor.apply()
    }

    val isLogined: Boolean
        get() = mSharedPreferences.getBoolean(PREFS_IS_LOGINED, false)

    fun saveUserSession(
        isRememberMe: Boolean = true,
        accessToken: String,
        userProfile: UserProfile
    ) {
        ApplicationGlobal.accessToken = accessToken
        if (isRememberMe) {
            mEditor.putBoolean(PREFS_IS_LOGINED, isRememberMe)
        }
        mEditor.putString(PREFS_ACCESS_TOKEN, accessToken)
        mEditor.putString(PREFS_USER_PROFILE, Gson().toJson(userProfile))
        mEditor.apply()
    }

    fun saveAccessToken(isRememberMe: Boolean = true, accessToken: String) {
        ApplicationGlobal.accessToken = accessToken
        if (isRememberMe) {
            mEditor.putBoolean(PREFS_IS_LOGINED, isRememberMe)
        }
        mEditor.putString(PREFS_ACCESS_TOKEN, accessToken)
        mEditor.apply()
    }

    val userProfile: UserProfile?
        get() = Gson().fromJson(
            mSharedPreferences.getString(PREFS_USER_PROFILE, ""),
            UserProfile::class.java
        )

    fun updateUserProfile(userProfile: UserProfile?) {
        if (null != userProfile) {
            mEditor.putString(PREFS_USER_PROFILE, Gson().toJson(userProfile))
            mEditor.apply()
        }
    }

    val accessToken: String
        get() = mSharedPreferences.getString(PREFS_ACCESS_TOKEN, "")!!

}