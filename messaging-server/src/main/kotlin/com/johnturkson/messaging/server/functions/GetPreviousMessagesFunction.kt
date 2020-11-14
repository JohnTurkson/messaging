package com.johnturkson.messaging.server.functions

import com.johnturkson.awstools.dynamodb.objectbuilder.buildDynamoDBObject
import com.johnturkson.awstools.dynamodb.requestbuilder.requests.QueryRequest
import com.johnturkson.messaging.server.configuration.DatabaseRequestHandler
import com.johnturkson.messaging.server.configuration.SerializerConfiguration
import com.johnturkson.messaging.server.data.Message
import com.johnturkson.messaging.server.lambda.WebsocketLambdaFunction
import com.johnturkson.messaging.server.lambda.WebsocketRequestContext
import com.johnturkson.messaging.server.requests.GetPreviousMessagesRequest
import com.johnturkson.messaging.server.responses.GetPreviousMessagesResponse
import com.johnturkson.messaging.server.responses.Response
import kotlinx.coroutines.runBlocking

class GetPreviousMessagesFunction : WebsocketLambdaFunction<GetPreviousMessagesRequest, GetPreviousMessagesResponse> {
    override val serializer = SerializerConfiguration.instance
    override val inputSerializer = GetPreviousMessagesRequest.serializer()
    override val outputSerializer = Response.serializer()
    
    override fun processRequest(
        request: GetPreviousMessagesRequest,
        context: WebsocketRequestContext,
    ): GetPreviousMessagesResponse {
        return runBlocking {
            getPreviousMessages(request.conversation, request.lastMessage)
        }
    }
    
    suspend fun getPreviousMessages(
        conversation: String,
        lastMessageId: String,
        limit: Int = 100,
    ): GetPreviousMessagesResponse {
        val lastMessage = GetMessageFunction().getMessage(lastMessageId).message
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
            exclusiveStartKey = buildDynamoDBObject {
                put("id", lastMessageId)
                put("conversation", conversation)
                put("time", lastMessage.time)
            },
            limit = limit
        )
        val response = DatabaseRequestHandler.instance.query(request, Message.serializer())
        return GetPreviousMessagesResponse(conversation, response.items)
    }
}
