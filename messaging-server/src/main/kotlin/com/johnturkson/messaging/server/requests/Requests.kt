package com.johnturkson.messaging.server.requests

import com.johnturkson.messaging.server.data.ConnectionData
import com.johnturkson.messaging.server.data.ConversationData
import com.johnturkson.messaging.server.data.MessageData
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Request

@Serializable
@SerialName("GetConnectionRequest")
data class GetConnectionRequest(val id: String) : Request()

@Serializable
@SerialName("GetConnectionsRequest")
object GetConnectionsRequest : Request()

@Serializable
@SerialName("CreateConnectionRequest")
data class CreateConnectionRequest(val data: ConnectionData) : Request()

@Serializable
@SerialName("DeleteConnectionRequest")
// TODO delete by id - make object (as the connection id is provided within the request context)
data class DeleteConnectionRequest(val data: ConnectionData) : Request()

@Serializable
@SerialName("GetMessageRequest")
data class GetMessageRequest(val id: String) : Request()

@Serializable
@SerialName("GetPreviousMessagesRequest")
data class GetPreviousMessagesRequest(val conversation: String, val lastMessage: String) : Request()

@Serializable
@SerialName("GetLatestMessagesRequest")
data class GetLatestMessagesRequest(val conversation: String) : Request()

@Serializable
@SerialName("CreateMessageRequest")
data class CreateMessageRequest(val data: MessageData) : Request()

@Serializable
@SerialName("GetConversationRequest")
data class GetConversationRequest(val id: String) : Request()

@Serializable
@SerialName("CreateConversationRequest")
data class CreateConversationRequest(val data: ConversationData) : Request()
