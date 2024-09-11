package com.example.sacrenachat.repository.models

data class PojoBackendResponse(
    val isSuccess: Boolean,
    val isSessionExpired: Boolean,
    val IsInternalError: Boolean
)