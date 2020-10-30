package com.johnturkson.messaging.server.requests

import com.johnturkson.messaging.server.data.ConnectionData
import com.johnturkson.messaging.server.data.MessageData
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Request

@Serializable
@SerialName("CreateConnectionRequest")
data class CreateConnectionRequest(val data: ConnectionData) : Request()

@Serializable
@SerialName("GetConnectionRequest")
object GetConnectionsRequest : Request()

@Serializable
@SerialName("DeleteConnectionRequest")
data class DeleteConnectionRequest(val data: ConnectionData) : Request()

@Serializable
@SerialName("CreateMessageRequest")
data class CreateMessageRequest(val data: MessageData) : Request()
