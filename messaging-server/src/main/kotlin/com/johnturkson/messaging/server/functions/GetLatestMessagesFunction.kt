package com.johnturkson.messaging.server.functions

import com.johnturkson.awstools.dynamodb.objectbuilder.buildDynamoDBObject
import com.johnturkson.awstools.dynamodb.requestbuilder.requests.QueryRequest
import com.johnturkson.messaging.common.data.Message
import com.johnturkson.messaging.common.requests.Request.GetLatestMessagesRequest
import com.johnturkson.messaging.common.responses.Response
import com.johnturkson.messaging.common.responses.Response.GetLatestMessagesResponse
import com.johnturkson.messaging.server.configuration.DatabaseRequestHandler
import com.johnturkson.messaging.server.configuration.SerializerConfiguration
import com.johnturkson.messaging.server.lambda.WebsocketLambdaFunction
import com.johnturkson.messaging.server.lambda.WebsocketRequestContext
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
            getLatestMessages(request.conversation, request.last)
        }
    }
    
    suspend fun getLatestMessages(conversation: String, last: String?, limit: Int = 100): GetLatestMessagesResponse {
        val lastMessage = if (last != null) GetMessageFunction().getMessage(last).message else null
        val table = "Messages"
        val index = "Latest"
        val request = QueryRequest(
            tableName = table,
            indexName = index,
            keyConditionExpression = "#conversation = :conversation",
            expressionAttributeNames = mapOf("#conversation" to "conversation"),
            expressionAttributeValues = buildDynamoDBObject {
                put(":conversation", conversation)
            },
            exclusiveStartKey = when {
                lastMessage != null -> buildDynamoDBObject {
                    put("id", lastMessage.id)
                    put("conversation", conversation)
                    put("created", lastMessage.created)
                }
                else -> null
            },
            limit = limit
        )
        val response = DatabaseRequestHandler.instance.query(request, Message.serializer())
        return GetLatestMessagesResponse(conversation, response.items)
    }
}
