package com.johnturkson.messaging.server.functions

import com.johnturkson.awstools.dynamodb.objectbuilder.buildDynamoDBObject
import com.johnturkson.awstools.dynamodb.requestbuilder.requests.GetItemRequest
import com.johnturkson.messaging.common.data.User
import com.johnturkson.messaging.common.requests.Request.GetUserRequest
import com.johnturkson.messaging.common.responses.Response
import com.johnturkson.messaging.common.responses.Response.GetUserResponse
import com.johnturkson.messaging.server.configuration.DatabaseRequestHandler
import com.johnturkson.messaging.server.configuration.SerializerConfiguration
import com.johnturkson.messaging.server.lambda.WebsocketLambdaFunction
import com.johnturkson.messaging.server.lambda.WebsocketRequestContext
import kotlinx.coroutines.runBlocking

class GetUserFunction : WebsocketLambdaFunction<GetUserRequest, GetUserResponse> {
    override val serializer = SerializerConfiguration.instance
    override val inputSerializer = GetUserRequest.serializer()
    override val outputSerializer = Response.serializer()
    
    override fun processRequest(request: GetUserRequest, context: WebsocketRequestContext): GetUserResponse {
        return runBlocking {
            getMessage(request.id)
        }
    }
    
    suspend fun getMessage(id: String): GetUserResponse {
        val table = "messages"
        val request = GetItemRequest<User>(
            tableName = table,
            key = buildDynamoDBObject {
                put("id", id)
            }
        )
        val response = DatabaseRequestHandler.instance.getItem(request, User.serializer())
        return GetUserResponse(response.item)
    }
}
