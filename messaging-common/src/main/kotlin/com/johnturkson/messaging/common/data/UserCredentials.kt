package com.johnturkson.messaging.common.data

import kotlinx.serialization.Serializable

@Serializable
data class UserCredentials(val id: String, val password: String)
