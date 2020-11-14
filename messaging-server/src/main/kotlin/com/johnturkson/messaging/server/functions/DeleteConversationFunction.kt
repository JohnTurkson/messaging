package com.johnturkson.messaging.server.functions

import com.johnturkson.awstools.dynamodb.objectbuilder.buildDynamoDBObject
import com.johnturkson.awstools.dynamodb.requestbuilder.requests.DeleteItemRequest
import com.johnturkson.messaging.server.configuration.DatabaseRequestHandler
import com.johnturkson.messaging.server.configuration.SerializerConfiguration
import com.johnturkson.messaging.common.data.Conversation
import com.johnturkson.messaging.server.lambda.WebsocketLambdaFunction
import com.johnturkson.messaging.server.lambda.WebsocketRequestContext
import com.johnturkson.messaging.common.requests.DeleteConversationRequest
import com.johnturkson.messaging.common.responses.DeleteConversationResponse
import com.johnturkson.messaging.common.responses.Response
import kotlinx.coroutines.runBlocking

class DeleteConversationFunction : WebsocketLambdaFunction<DeleteConversationRequest, DeleteConversationResponse> {
    override val serializer = SerializerConfiguration.instance
    override val inputSerializer = DeleteConversationRequest.serializer()
    override val outputSerializer = Response.serializer()
    
    override fun processRequest(
        request: DeleteConversationRequest,
        context: WebsocketRequestContext,
    ): DeleteConversationResponse {
        return runBlocking {
            deleteConversation(request.id)
        }
    }
    
    suspend fun deleteConversation(id: String): DeleteConversationResponse {
        val table = "messages"
        val request = DeleteItemRequest<Conversation>(
            tableName = table,
            key = buildDynamoDBObject {
                put("id", id)
            }
        )
        DatabaseRequestHandler.instance.deleteItem(request, Conversation.serializer())
        return DeleteConversationResponse
    }
}
