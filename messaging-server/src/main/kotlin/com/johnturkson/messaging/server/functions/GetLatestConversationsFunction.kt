package com.johnturkson.messaging.server.functions

import com.johnturkson.awstools.dynamodb.objectbuilder.buildDynamoDBObject
import com.johnturkson.awstools.dynamodb.requestbuilder.requests.QueryRequest
import com.johnturkson.messaging.common.data.Conversation
import com.johnturkson.messaging.common.requests.Request.GetLatestConversationsRequest
import com.johnturkson.messaging.common.responses.Response
import com.johnturkson.messaging.common.responses.Response.GetLatestConversationsResponse
import com.johnturkson.messaging.server.configuration.DatabaseRequestHandler
import com.johnturkson.messaging.server.configuration.SerializerConfiguration
import com.johnturkson.messaging.server.lambda.WebsocketLambdaFunction
import com.johnturkson.messaging.server.lambda.WebsocketRequestContext
import kotlinx.coroutines.runBlocking

class GetLatestConversationsFunction :
    WebsocketLambdaFunction<GetLatestConversationsRequest, GetLatestConversationsResponse> {
    override val serializer = SerializerConfiguration.instance
    override val inputSerializer = GetLatestConversationsRequest.serializer()
    override val outputSerializer = Response.serializer()
    
    override fun processRequest(
        request: GetLatestConversationsRequest,
        context: WebsocketRequestContext,
    ): GetLatestConversationsResponse {
        return runBlocking {
            getLatestConversations(request.user, request.last)
        }
    }
    
    suspend fun getLatestConversations(user: String, last: String?, limit: Int = 100): GetLatestConversationsResponse {
        val lastConversation = if (last != null) GetConversationFunction().getConversation(last).conversation else null
        val request = QueryRequest(
            tableName = "UserConversations",
            indexName = "Latest",
            keyConditionExpression = "#user = :user",
            expressionAttributeNames = mapOf("#user" to "user"),
            expressionAttributeValues = buildDynamoDBObject {
                put(":user", user)
            },
            exclusiveStartKey = when {
                lastConversation != null -> buildDynamoDBObject { 
                    put("user", user)
                    put("conversation", lastConversation.id)
                    put("modified", lastConversation.modified)
                }
                else -> null
            },
            limit = limit,
        )
        val response = DatabaseRequestHandler.instance.query(request, Conversation.serializer())
        return GetLatestConversationsResponse(user, response.items)
    }
}
