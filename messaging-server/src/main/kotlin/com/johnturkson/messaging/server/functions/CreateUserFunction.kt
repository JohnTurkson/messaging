package com.johnturkson.messaging.server.functions

import com.johnturkson.awstools.dynamodb.request.PutItemRequest
import com.johnturkson.messaging.server.configuration.DatabaseRequestHandler
import com.johnturkson.messaging.server.configuration.SerializerConfiguration
import com.johnturkson.messaging.server.data.User
import com.johnturkson.messaging.server.data.UserData
import com.johnturkson.messaging.server.lambda.WebsocketLambdaFunction
import com.johnturkson.messaging.server.lambda.WebsocketRequestContext
import com.johnturkson.messaging.server.requests.CreateUserRequest
import com.johnturkson.messaging.server.responses.CreateUserResponse
import com.johnturkson.messaging.server.responses.Response
import kotlinx.coroutines.runBlocking
import kotlin.random.Random
import kotlin.random.nextInt

class CreateUserFunction : WebsocketLambdaFunction<CreateUserRequest, CreateUserResponse> {
    override val serializer = SerializerConfiguration.instance
    override val inputSerializer = CreateUserRequest.serializer()
    override val outputSerializer = Response.serializer()
    
    override fun processRequest(request: CreateUserRequest, context: WebsocketRequestContext): CreateUserResponse {
        return runBlocking {
            createUser(generateUserId(), request.data)
        }
    }
    
    fun generateUserId(length: Int = 16): String {
        var id = ""
        repeat(length) { id += Random.nextInt(0..0xf).toString(0x10) }
        return id
    }
    
    suspend fun createUser(id: String, data: UserData): CreateUserResponse {
        val user = User(id, data.username, data.email)
        val table = "users"
        val request = PutItemRequest(
            tableName = table,
            item = user
        )
        val response = DatabaseRequestHandler.instance.putItem(request, User.serializer())
        return CreateUserResponse(user)
    }
}
