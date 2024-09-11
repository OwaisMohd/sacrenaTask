package com.example.sacrenachat.repository.models

import androidx.annotation.StringRes

data class PojoMessage(@StringRes val resId: Int? = null, val message: String? = null)