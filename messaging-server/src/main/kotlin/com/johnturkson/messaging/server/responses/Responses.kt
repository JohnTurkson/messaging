package com.johnturkson.messaging.server.responses

import com.johnturkson.messaging.server.data.Connection
import com.johnturkson.messaging.server.data.Message
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Response

@Serializable
@SerialName("CreateConnectionResponse")
data class CreateConnectionResponse(val connection: Connection) : Response()

@Serializable
@SerialName("GetConnectionsResponse")
data class GetConnectionsResponse(val data: List<Connection>) : Response()

@Serializable
@SerialName("DeleteConnectionResponse")
object DeleteConnectionResponse : Response()

@Serializable
@SerialName("CreateMessageResponse")
data class CreateMessageResponse(val data: Message) : Response()

