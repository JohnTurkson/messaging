package com.johnturkson.messaging.common.requests

import com.johnturkson.messaging.common.data.ConnectionData
import com.johnturkson.messaging.common.data.ConversationData
import com.johnturkson.messaging.common.data.MessageData
import com.johnturkson.messaging.common.data.UserData
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Request {
    @Serializable
    @SerialName("CreateUserRequest")
    data class CreateUserRequest(val data: UserData, val password: String) : Request()
    
    @Serializable
    @SerialName("CreateConnectionRequest")
    data class CreateConnectionRequest(val data: ConnectionData) : Request()
    
    @Serializable
    @SerialName("CreateMessageRequest")
    data class CreateMessageRequest(val data: MessageData) : Request()
    
    @Serializable
    @SerialName("CreateConversationRequest")
    data class CreateConversationRequest(val data: ConversationData) : Request()
    
    @Serializable
    @SerialName("GetUserRequest")
    data class GetUserRequest(val id: String) : Request()
    
    @Serializable
    @SerialName("GetConnectionRequest")
    data class GetConnectionRequest(val id: String) : Request()
    
    @Serializable
    @SerialName("GetConversationRequest")
    data class GetConversationRequest(val id: String) : Request()
    
    @Serializable
    @SerialName("GetLatestConversationsRequest")
    data class GetLatestConversationsRequest(val user: String, val last: String? = null) : Request()
    
    @Serializable
    @SerialName("GetConversationUsersRequest")
    data class GetConversationUsersRequest(val conversation: String, val last: String? = null): Request()
    
    @Serializable
    @SerialName("GetMessageRequest")
    data class GetMessageRequest(val id: String) : Request()
    
    @Serializable
    @SerialName("GetLatestMessagesRequest")
    data class GetLatestMessagesRequest(val conversation: String, val last: String? = null) : Request()
    
    @Serializable
    @SerialName("DeleteUserRequest")
    data class DeleteUserRequest(val id: String) : Request()
    
    @Serializable
    @SerialName("DeleteConnectionRequest")
    data class DeleteConnectionRequest(val id: String) : Request()
    
    @Serializable
    @SerialName("DeleteMessageRequest")
    data class DeleteMessageRequest(val id: String) : Request()
    
    @Serializable
    @SerialName("DeleteConversationRequest")
    data class DeleteConversationRequest(val id: String) : Request()
}
