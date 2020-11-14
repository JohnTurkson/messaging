package com.johnturkson.messaging.server.data

import kotlinx.serialization.Serializable

@Serializable
data class UserData(val username: String, val email: String)
