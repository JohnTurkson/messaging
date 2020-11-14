package com.johnturkson.messaging.server.functions

import com.johnturkson.awstools.dynamodb.objectbuilder.buildDynamoDBObject
import com.johnturkson.awstools.dynamodb.requestbuilder.requests.QueryRequest
import com.johnturkson.messaging.server.configuration.DatabaseRequestHandler
import com.johnturkson.messaging.server.configuration.SerializerConfiguration
import com.johnturkson.messaging.server.data.Message
import com.johnturkson.messaging.server.lambda.WebsocketLambdaFunction
import com.johnturkson.messaging.server.lambda.WebsocketRequestContext
import com.johnturkson.messaging.server.requests.GetLatestMessagesRequest
import com.johnturkson.messaging.server.responses.GetLatestMessagesResponse
import com.johnturkson.messaging.server.responses.Response
import kotlinx.coroutines.runBlocking

class GetLatestMessagesFunction : WebsocketLambdaFunction<GetLatestMessagesRequest, GetLatestMessagesResponse> {
    override val serializer = SerializerConfiguration.instance
    override val inputSerializer = GetLatestMessagesRequest.serializer()
    override val outputSerializer = Response.serializer()
    
    override fun processRequest(
        request: GetLatestMessagesRequest,
        context: WebsocketRequestContext,
    ): GetLatestMessagesResponse {
        return runBlocking {
            getLatestMessages(request.conversation)
        }
    }
    
    suspend fun getLatestMessages(conversation: String, limit: Int = 100): GetLatestMessagesResponse {
        val table = "messages"
        val index = "conversation"
        val request = QueryRequest<Message>(
            tableName = table,
            indexName = index,
            keyConditionExpression = "#conversation = :conversation",
            expressionAttributeNames = mapOf("#conversation" to "conversation"),
            expressionAttributeValues = buildDynamoDBObject {
                put(":conversation", conversation)
            },
            limit = limit
        )
        val response = DatabaseRequestHandler.instance.query(request, Message.serializer())
        return GetLatestMessagesResponse(conversation, response.items)
    }
}
