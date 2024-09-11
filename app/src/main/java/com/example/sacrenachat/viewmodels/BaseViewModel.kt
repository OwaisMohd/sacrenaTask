package com.example.sacrenachat.viewmodels

import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.sacrenachat.R
import com.example.sacrenachat.repository.models.PojoMessage
import com.example.sacrenachat.repository.models.UserProfile
import com.example.sacrenachat.repository.networkrequest.RetrofitRequest
import com.example.sacrenachat.repository.preferences.UserPrefsManager
import kotlinx.coroutines.CoroutineExceptionHandler

abstract class BaseViewModel(application: Application) : AndroidViewModel(application) {

    //protected val mCompositeDisposable: CompositeDisposable by lazy { CompositeDisposable() }
    protected val mUserPrefsManager: UserPrefsManager by lazy { UserPrefsManager(getApplication()) }
    protected val isShowLoader = MutableLiveData<Boolean>()
    protected val isShowNoDataText = MutableLiveData<Boolean>()
    protected val isShowSwipeRefreshLayout = MutableLiveData<Boolean>()
    protected val isSessionExpired = MutableLiveData<Boolean>()
    protected val showMessage = MutableLiveData<PojoMessage>()
    protected val errorHandler = MutableLiveData<ErrorHandler>()
    protected val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        //If there is some error parsing the request itself in Interactor
        isShowSwipeRefreshLayout.postValue(false)
        isShowLoader.postValue(false)
        throwable.printStackTrace()
        showMessage.postValue(PojoMessage(resId = RetrofitRequest.getRetrofitError(throwable)))
    }

    fun isShowLoader(): LiveData<Boolean> = isShowLoader

    fun isShowNoDataText(): LiveData<Boolean> = isShowNoDataText

    fun isSessionExpired(): LiveData<Boolean> = isSessionExpired

    fun isShowSwipeRefreshLayout(): LiveData<Boolean> = isShowSwipeRefreshLayout

    fun getMessage(): LiveData<PojoMessage> = showMessage

    fun getErrorHandler(): LiveData<ErrorHandler> = errorHandler

    //To see if the user is already logged in or not.
    fun isLoggedIn(): Boolean {
        return try {
            mUserPrefsManager.isLogined
        } catch (e: Exception) {
            false
        }
    }

    //Get data from SharedPrefs
    fun getUserDataFromSharedPrefs(): UserProfile {
        return if (isLoggedIn()) {
            val user = mUserPrefsManager.userProfile
            if (user != null) {
                user
            } else {
                mUserPrefsManager.clearUserPrefs()
                isSessionExpired.value = true
                UserProfile()
            }
        } else {
            UserProfile()
        }
    }

    enum class ErrorHandler(@StringRes private val resourceId: Int) : ErrorEvent {
        EMPTY_PHONE_NUMBER(R.string.empty_phone_no),
        EMPTY_OTP(R.string.empty_otp),
        EMPTY_COUNTRY_CODE(R.string.empty_country_code),
        EMPTY_OLD_PASSWORD(R.string.empty_old_password),
        EMPTY_NEW_PASSWORD(R.string.empty_new_password),
        INVALID_NEW_PASSWORD(R.string.invalid_new_password),

        //        EMPTY_DISPLAY_NAME(R.string.empty_display_name),
//        EMPTY_NAME(R.string.empty_name),
//        EMPTY_REGION_ID_LIST(R.string.empty_region_list),
        PASSWORD_NOT_MATCHED(R.string.password_not_matched);

        override fun getErrorResource() = resourceId
    }

    interface ErrorEvent {
        @StringRes
        fun getErrorResource(): Int
    }
}