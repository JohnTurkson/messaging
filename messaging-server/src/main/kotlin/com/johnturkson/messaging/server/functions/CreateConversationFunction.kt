package com.johnturkson.messaging.server.functions

import com.johnturkson.awstools.dynamodb.request.PutItemRequest
import com.johnturkson.awstools.signer.AWSRequestSigner
import com.johnturkson.messaging.server.data.Conversation
import com.johnturkson.messaging.server.data.ConversationData
import com.johnturkson.messaging.server.lambda.WebsocketLambdaFunction
import com.johnturkson.messaging.server.lambda.WebsocketRequestContext
import com.johnturkson.messaging.server.requests.CreateConversationRequest
import com.johnturkson.messaging.server.responses.CreateConversationResponse
import com.johnturkson.messaging.server.responses.Response
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.URL
import kotlin.random.Random
import kotlin.random.nextInt

class CreateConversationFunction : WebsocketLambdaFunction<CreateConversationRequest, CreateConversationResponse> {
    override val configuration = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
    override val inputSerializer = CreateConversationRequest.serializer()
    override val outputSerializer = Response.serializer()
    
    override fun processRequest(
        request: CreateConversationRequest,
        context: WebsocketRequestContext,
    ): CreateConversationResponse {
        return CreateConversationResponse(createConversation(request.data))
    }
    
    fun generateConversationId(length: Int = 16): String {
        var id = ""
        repeat(length) { id += Random.nextInt(0..0xf).toString(0x10) }
        return id
    }
    
    fun createConversation(data: ConversationData): Conversation {
        val accessKeyId = System.getenv("AWS_ACCESS_KEY_ID")
        val secretKey = System.getenv("AWS_SECRET_ACCESS_KEY")
        val sessionToken = System.getenv("AWS_SESSION_TOKEN")
        
        val region = "us-west-2"
        val service = "dynamodb"
        val method = "POST"
        val url = "https://dynamodb.us-west-2.amazonaws.com"
        
        val id = generateConversationId()
        // TODO check current user is contained in members
        // TODO check all members exist
        // TODO check user has permission to add member to conversation
        val conversation = Conversation(id, data.members)
        
        val table = "conversations"
        val request = PutItemRequest(table, conversation)
        val body = configuration.encodeToString(PutItemRequest.serializer(Conversation.serializer()), request)
        
        val headers = listOf(
            AWSRequestSigner.Header("X-Amz-Security-Token", sessionToken),
            AWSRequestSigner.Header("X-Amz-Target", "DynamoDB_20120810.PutItem"),
        )
        
        val signedHeaders = AWSRequestSigner.generateRequestHeaders(
            accessKeyId,
            secretKey,
            region,
            service,
            method,
            url,
            body,
            headers,
        )
        
        val call = Request.Builder()
            .url(URL(url))
            .apply { signedHeaders.forEach { (name, value) -> addHeader(name, value) } }
            .method(method, body.toRequestBody("application/json".toMediaType()))
            .build()
        
        // TODO make singleton client
        val client = OkHttpClient()
        
        client.newCall(call).execute().close()
        
        return conversation
    }
}
