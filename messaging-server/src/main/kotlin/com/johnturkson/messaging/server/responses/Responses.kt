package com.johnturkson.messaging.server.responses

import com.johnturkson.messaging.server.data.Connection
import com.johnturkson.messaging.server.data.Conversation
import com.johnturkson.messaging.server.data.Message
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Response

@Serializable
@SerialName("GetConnectionResponse")
data class GetConnectionResponse(val connection: Connection) : Response()

@Serializable
@SerialName("GetConnectionsResponse")
data class GetConnectionsResponse(val connections: List<Connection>) : Response()

@Serializable
@SerialName("CreateConnectionResponse")
data class CreateConnectionResponse(val connection: Connection) : Response()

@Serializable
@SerialName("DeleteConnectionResponse")
// TODO return deleted object
object DeleteConnectionResponse : Response()

@Serializable
@SerialName("GetMessageResponse")
data class GetMessageResponse(val message: Message) : Response()

@Serializable
@SerialName("GetPreviousMessagesResponse")
data class GetPreviousMessagesResponse(val conversation: String, val messages: List<Message>) : Response()

@Serializable
@SerialName("GetLatestMessagesResponse")
data class GetLatestMessagesResponse(val conversation: String, val messages: List<Message>) : Response()

@Serializable
@SerialName("CreateMessageResponse")
data class CreateMessageResponse(val message: Message) : Response()

@Serializable
@SerialName("GetConversationResponse")
data class GetConversationResponse(val conversation: Conversation) : Response()

@Serializable
@SerialName("CreateConversationResponse")
data class CreateConversationResponse(val conversation: Conversation) : Response()
