package com.johnturkson.messaging.common.responses

import com.johnturkson.messaging.common.data.Connection
import com.johnturkson.messaging.common.data.Conversation
import com.johnturkson.messaging.common.data.Message
import com.johnturkson.messaging.common.data.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Response

@Serializable
@SerialName("CreateUserResponse")
data class CreateUserResponse(val user: User) : Response()

@Serializable
@SerialName("CreateConnectionResponse")
data class CreateConnectionResponse(val connection: Connection) : Response()

@Serializable
@SerialName("CreateMessageResponse")
data class CreateMessageResponse(val message: Message) : Response()

@Serializable
@SerialName("CreateConversationResponse")
data class CreateConversationResponse(val conversation: Conversation) : Response()

@Serializable
@SerialName("GetUserResponse")
data class GetUserResponse(val user: User) : Response()

@Serializable
@SerialName("GetConnectionResponse")
data class GetConnectionResponse(val connection: Connection) : Response()

@Serializable
@SerialName("GetMessageResponse")
data class GetMessageResponse(val message: Message) : Response()

@Serializable
@SerialName("GetLatestMessagesResponse")
data class GetLatestMessagesResponse(val conversation: String, val messages: List<Message>) : Response()

@Serializable
@SerialName("GetPreviousMessagesResponse")
data class GetPreviousMessagesResponse(val conversation: String, val messages: List<Message>) : Response()

@Serializable
@SerialName("GetConversationResponse")
data class GetConversationResponse(val conversation: Conversation) : Response()

@Serializable
@SerialName("DeleteUserResponse")
object DeleteUserResponse : Response()

@Serializable
@SerialName("DeleteConnectionResponse")
object DeleteConnectionResponse : Response()

@Serializable
@SerialName("DeleteMessageResponse")
object DeleteMessageResponse : Response()

@Serializable
@SerialName("DeleteConversationResponse")
object DeleteConversationResponse : Response()
