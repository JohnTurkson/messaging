package com.johnturkson.messaging.server.functions

import com.johnturkson.awstools.dynamodb.objectbuilder.buildDynamoDBObject
import com.johnturkson.awstools.dynamodb.requestbuilder.requests.DeleteItemRequest
import com.johnturkson.messaging.common.data.Message
import com.johnturkson.messaging.common.requests.Request.DeleteMessageRequest
import com.johnturkson.messaging.common.responses.Response
import com.johnturkson.messaging.common.responses.Response.DeleteMessageResponse
import com.johnturkson.messaging.server.configuration.DatabaseRequestHandler
import com.johnturkson.messaging.server.configuration.SerializerConfiguration
import com.johnturkson.messaging.server.lambda.WebsocketLambdaFunction
import com.johnturkson.messaging.server.lambda.WebsocketRequestContext
import kotlinx.coroutines.runBlocking

class DeleteMessageFunction : WebsocketLambdaFunction<DeleteMessageRequest, DeleteMessageResponse> {
    override val serializer = SerializerConfiguration.instance
    override val inputSerializer = DeleteMessageRequest.serializer()
    override val outputSerializer = Response.serializer()
    
    override fun processRequest(
        request: DeleteMessageRequest,
        context: WebsocketRequestContext,
    ): DeleteMessageResponse {
        return runBlocking {
            deleteMessage(request.id)
        }
    }
    
    suspend fun deleteMessage(id: String): DeleteMessageResponse {
        val table = "messages"
        val request = DeleteItemRequest(
            tableName = table,
            key = buildDynamoDBObject {
                put("id", id)
            }
        )
        DatabaseRequestHandler.instance.deleteItem(request, Message.serializer())
        return DeleteMessageResponse
    }
}
