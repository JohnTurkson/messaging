package com.johnturkson.messaging.server.functions

import com.johnturkson.awstools.dynamodb.requestbuilder.requests.PutItemRequest
import com.johnturkson.messaging.common.data.User
import com.johnturkson.messaging.common.data.UserCredentials
import com.johnturkson.messaging.common.data.UserData
import com.johnturkson.messaging.common.requests.Request.CreateUserRequest
import com.johnturkson.messaging.common.responses.Response
import com.johnturkson.messaging.common.responses.Response.CreateUserResponse
import com.johnturkson.messaging.server.configuration.DatabaseRequestHandler
import com.johnturkson.messaging.server.configuration.SerializerConfiguration
import com.johnturkson.messaging.server.lambda.WebsocketLambdaFunction
import com.johnturkson.messaging.server.lambda.WebsocketRequestContext
import kotlinx.coroutines.runBlocking
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.security.MessageDigest
import java.util.Base64
import kotlin.random.Random
import kotlin.random.nextInt

class CreateUserFunction : WebsocketLambdaFunction<CreateUserRequest, CreateUserResponse> {
    override val serializer = SerializerConfiguration.instance
    override val inputSerializer = CreateUserRequest.serializer()
    override val outputSerializer = Response.serializer()
    
    override fun processRequest(request: CreateUserRequest, context: WebsocketRequestContext): CreateUserResponse {
        return runBlocking {
            createUser(generateUserId(), request.data, generatePasswordHash(request.password))
        }
    }
    
    fun generateUserId(length: Int = 16): String {
        var id = ""
        repeat(length) { id += Random.nextInt(0..0xf).toString(0x10) }
        return id
    }
    
    fun generatePasswordHash(password: String): String {
        val prehashAlgorithm = "SHA-512"
        val prehashBytes = MessageDigest.getInstance(prehashAlgorithm).digest(password.toByteArray())
        val prehashHex = Base64.getEncoder().encodeToString(prehashBytes)
        val encoder = BCryptPasswordEncoder()
        return encoder.encode(prehashHex)
    }
    
    suspend fun createUser(id: String, data: UserData, password: String): CreateUserResponse {
        // TODO check if user exists
        val user = User(id, data.username, data.email)
        val credentials = UserCredentials(id, password)
        val createUser = PutItemRequest(
            tableName = "users",
            item = user,
        )
        val createUserResponse = DatabaseRequestHandler.instance.putItem(
            createUser,
            User.serializer(),
        )
        val createCredentials = PutItemRequest(
            tableName = "credentials",
            item = credentials,
        )
        val createCredentialsResponse = DatabaseRequestHandler.instance.putItem(
            createCredentials,
            UserCredentials.serializer(),
        )
        return CreateUserResponse(user)
    }
}
