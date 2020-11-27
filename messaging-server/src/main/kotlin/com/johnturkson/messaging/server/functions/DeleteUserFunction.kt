package com.johnturkson.messaging.server.functions

import com.johnturkson.awstools.dynamodb.objectbuilder.buildDynamoDBObject
import com.johnturkson.awstools.dynamodb.requestbuilder.requests.DeleteItemRequest
import com.johnturkson.messaging.common.data.User
import com.johnturkson.messaging.common.requests.Request.DeleteUserRequest
import com.johnturkson.messaging.common.responses.Response
import com.johnturkson.messaging.common.responses.Response.DeleteUserResponse
import com.johnturkson.messaging.server.configuration.DatabaseRequestHandler
import com.johnturkson.messaging.server.configuration.SerializerConfiguration
import com.johnturkson.messaging.server.lambda.WebsocketLambdaFunction
import com.johnturkson.messaging.server.lambda.WebsocketRequestContext
import kotlinx.coroutines.runBlocking

class DeleteUserFunction : WebsocketLambdaFunction<DeleteUserRequest, DeleteUserResponse> {
    override val serializer = SerializerConfiguration.instance
    override val inputSerializer = DeleteUserRequest.serializer()
    override val outputSerializer = Response.serializer()
    
    override fun processRequest(request: DeleteUserRequest, context: WebsocketRequestContext): DeleteUserResponse {
        return runBlocking {
            deleteUser(request.id)
        }
    }
    
    suspend fun deleteUser(id: String): DeleteUserResponse {
        val table = "users"
        val request = DeleteItemRequest<User>(
            tableName = table,
            key = buildDynamoDBObject {
                put("id", id)
            }
        )
        DatabaseRequestHandler.instance.deleteItem(request, User.serializer())
        return DeleteUserResponse
    }
}
