package com.johnturkson.messaging.server.functions

import com.johnturkson.awstools.dynamodb.objectbuilder.buildDynamoDBObject
import com.johnturkson.awstools.dynamodb.requestbuilder.requests.GetItemRequest
import com.johnturkson.messaging.server.configuration.DatabaseRequestHandler
import com.johnturkson.messaging.server.configuration.SerializerConfiguration
import com.johnturkson.messaging.server.data.Message
import com.johnturkson.messaging.server.lambda.WebsocketLambdaFunction
import com.johnturkson.messaging.server.lambda.WebsocketRequestContext
import com.johnturkson.messaging.server.requests.GetMessageRequest
import com.johnturkson.messaging.server.responses.GetMessageResponse
import com.johnturkson.messaging.server.responses.Response
import kotlinx.coroutines.runBlocking

class GetMessageFunction : WebsocketLambdaFunction<GetMessageRequest, GetMessageResponse> {
    override val serializer = SerializerConfiguration.instance
    override val inputSerializer = GetMessageRequest.serializer()
    override val outputSerializer = Response.serializer()
    
    override fun processRequest(request: GetMessageRequest, context: WebsocketRequestContext): GetMessageResponse {
        return runBlocking {
            getMessage(request.id)
        }
    }
    
    suspend fun getMessage(id: String): GetMessageResponse {
        val table = "messages"
        val request = GetItemRequest<Message>(
            tableName = table,
            key = buildDynamoDBObject {
                put("id", id)
            }
        )
        val response = DatabaseRequestHandler.instance.getItem(request, Message.serializer())
        return GetMessageResponse(response.item)
    }
}
