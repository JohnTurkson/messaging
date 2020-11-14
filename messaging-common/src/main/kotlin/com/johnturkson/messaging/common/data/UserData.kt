package com.johnturkson.messaging.common.data

import kotlinx.serialization.Serializable

@Serializable
data class UserData(val username: String, val email: String)
