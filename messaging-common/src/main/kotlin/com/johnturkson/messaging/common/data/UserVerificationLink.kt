package com.johnturkson.messaging.common.data

import kotlinx.serialization.Serializable

@Serializable
data class UserVerificationLink(val id: String, val link: String)
