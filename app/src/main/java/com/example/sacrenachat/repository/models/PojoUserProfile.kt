package com.example.sacrenachat.repository.models

data class PojoUserProfile(
    val code: Int = 0,
    val data: Data = Data(),
    val message: String = ""
)

data class Data(
    val accessToken: String = "",
    val user: UserProfile = UserProfile()
)

data class PojoUserUpdate(
    val code: Int = 0,
    val data: UserProfile = UserProfile(),
    val message: String = ""
)

data class UserProfile(
    var _id: String = "",
    var displayName: String = "",
    var notifications: Int = 0,
    var otp: Int = 0,
    var mobile: Phone = Phone(),
    var picture: String = "",
    var name: String = "",
    var primaryEmail: String = "",
    var primaryEmailVerified: Boolean = false,
    var secondaryEmail: String = "",
    var secondaryEmailVerified: Boolean = false,
    var conversationId: String = "",
    var biography: String = "",
    var maxVideoSize: Int = 50
)

data class Phone(
    var code: String = "",
    var number: Long = 0L,
    var isVerified: Boolean = false
)

data class PojoDate(
    var day: Int = 0,
    var month: Int = 0,
    var year: Int = 0
)

