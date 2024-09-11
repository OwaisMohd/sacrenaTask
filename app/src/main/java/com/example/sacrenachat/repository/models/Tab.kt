package com.example.sacrenachat.repository.models

data class Tab(
    val tabFragment: androidx.fragment.app.Fragment, val tabName: String = "", val tabIcon: Int?,
    val isShowTabName: Boolean
)