package com.johnturkson.messaging.server.functions

import com.johnturkson.awstools.dynamodb.objectbuilder.buildDynamoDBObject
import com.johnturkson.awstools.dynamodb.requestbuilder.requests.GetItemRequest
import com.johnturkson.messaging.common.data.Conversation
import com.johnturkson.messaging.common.requests.Request.GetConversationRequest
import com.johnturkson.messaging.common.responses.Response
import com.johnturkson.messaging.common.responses.Response.GetConversationResponse
import com.johnturkson.messaging.server.configuration.DatabaseRequestHandler
import com.johnturkson.messaging.server.configuration.SerializerConfiguration
import com.johnturkson.messaging.server.lambda.WebsocketLambdaFunction
import com.johnturkson.messaging.server.lambda.WebsocketRequestContext
import kotlinx.coroutines.runBlocking

class GetConversationFunction : WebsocketLambdaFunction<GetConversationRequest, GetConversationResponse> {
    override val serializer = SerializerConfiguration.instance
    override val inputSerializer = GetConversationRequest.serializer()
    override val outputSerializer = Response.serializer()
    
    override fun processRequest(
        request: GetConversationRequest,
        context: WebsocketRequestContext,
    ): GetConversationResponse {
        return runBlocking {
            getConversation(request.id)
        }
    }
    
    suspend fun getConversation(id: String): GetConversationResponse {
        val table = "conversations"
        val request = GetItemRequest<Conversation>(
            tableName = table,
            key = buildDynamoDBObject {
                put("id", id)
            }
        )
        val response = DatabaseRequestHandler.instance.getItem(request, Conversation.serializer())
        return GetConversationResponse(response.item)
    }
}
