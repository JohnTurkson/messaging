package com.johnturkson.messaging.server.functions

import com.johnturkson.awstools.dynamodb.objectbuilder.buildDynamoDBObject
import com.johnturkson.awstools.dynamodb.request.GetItemRequest
import com.johnturkson.messaging.server.configuration.DatabaseRequestHandler
import com.johnturkson.messaging.server.configuration.SerializerConfiguration
import com.johnturkson.messaging.server.data.Conversation
import com.johnturkson.messaging.server.lambda.WebsocketLambdaFunction
import com.johnturkson.messaging.server.lambda.WebsocketRequestContext
import com.johnturkson.messaging.server.requests.GetConversationRequest
import com.johnturkson.messaging.server.responses.GetConversationResponse
import com.johnturkson.messaging.server.responses.Response
import kotlinx.coroutines.runBlocking

class GetConversationFunction : WebsocketLambdaFunction<GetConversationRequest, GetConversationResponse> {
    override val serializer = SerializerConfiguration.instance
    override val inputSerializer = GetConversationRequest.serializer()
    override val outputSerializer = Response.serializer()
    
    override fun processRequest(
        request: GetConversationRequest,
        context: WebsocketRequestContext,
    ): GetConversationResponse {
        return runBlocking { GetConversationResponse(getConversation(request.id)) }
    }
    
    suspend fun getConversation(id: String): Conversation {
        val table = "conversations"
        val request = GetItemRequest<Conversation>(
            tableName = table,
            key = buildDynamoDBObject {
                put("id", id)
            }
        )
        
        val response = DatabaseRequestHandler.instance.getItem(request, Conversation.serializer())
        
        return response.item
    }
}