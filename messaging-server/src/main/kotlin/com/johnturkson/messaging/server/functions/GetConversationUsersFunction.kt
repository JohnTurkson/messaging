package com.johnturkson.messaging.server.functions

import com.johnturkson.awstools.dynamodb.objectbuilder.buildDynamoDBObject
import com.johnturkson.awstools.dynamodb.requestbuilder.requests.QueryRequest
import com.johnturkson.messaging.common.data.User
import com.johnturkson.messaging.common.requests.Request.GetConversationUsersRequest
import com.johnturkson.messaging.common.responses.Response
import com.johnturkson.messaging.common.responses.Response.GetConversationUsersResponse
import com.johnturkson.messaging.server.configuration.DatabaseRequestHandler
import com.johnturkson.messaging.server.configuration.SerializerConfiguration
import com.johnturkson.messaging.server.lambda.WebsocketLambdaFunction
import com.johnturkson.messaging.server.lambda.WebsocketRequestContext
import kotlinx.coroutines.runBlocking

class GetConversationUsersFunction :
    WebsocketLambdaFunction<GetConversationUsersRequest, GetConversationUsersResponse> {
    override val serializer = SerializerConfiguration.instance
    override val inputSerializer = GetConversationUsersRequest.serializer()
    override val outputSerializer = Response.serializer()
    
    override fun processRequest(
        request: GetConversationUsersRequest,
        context: WebsocketRequestContext,
    ): GetConversationUsersResponse {
        return runBlocking {
            getConversationUsers(request.conversation, request.last)
        }
    }
    
    suspend fun getConversationUsers(
        conversation: String,
        last: String?,
        limit: Int = 100,
    ): GetConversationUsersResponse {
        val lastUser = if (last != null) GetUserFunction().getUser(last).user else null
        val request = QueryRequest(
            tableName = "ConversationUsers",
            keyConditionExpression = "#conversation = :conversation",
            expressionAttributeNames = mapOf("#conversation" to "conversation"),
            expressionAttributeValues = buildDynamoDBObject {
                put(":conversation", conversation)
            },
            exclusiveStartKey = when {
                lastUser != null -> buildDynamoDBObject {
                    put("conversation", conversation)
                    put("user", lastUser.id)
                }
                else -> null
            },
            limit = limit,
        )
        val response = DatabaseRequestHandler.instance.query(request, User.serializer())
        return GetConversationUsersResponse(conversation, response.items)
    }
}
