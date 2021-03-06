package com.johnturkson.messaging.common.data

import kotlinx.serialization.Serializable

@Serializable
data class User(val id: String, val username: String, val email: String, val verified: Boolean)
