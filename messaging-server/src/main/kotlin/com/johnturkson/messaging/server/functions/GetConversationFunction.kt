package com.johnturkson.messaging.server.functions

import com.johnturkson.awstools.dynamodb.objectbuilder.buildDynamoDBObject
import com.johnturkson.awstools.dynamodb.request.GetItemRequest
import com.johnturkson.awstools.dynamodb.request.GetItemResponse
import com.johnturkson.awstools.signer.AWSRequestSigner
import com.johnturkson.messaging.server.data.Conversation
import com.johnturkson.messaging.server.lambda.WebsocketLambdaFunction
import com.johnturkson.messaging.server.lambda.WebsocketRequestContext
import com.johnturkson.messaging.server.requests.GetConversationRequest
import com.johnturkson.messaging.server.responses.GetConversationResponse
import com.johnturkson.messaging.server.responses.Response
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.URL

class GetConversationFunction : WebsocketLambdaFunction<GetConversationRequest, GetConversationResponse> {
    override val configuration = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
    override val inputSerializer = GetConversationRequest.serializer()
    override val outputSerializer = Response.serializer()
    
    override fun processRequest(
        request: GetConversationRequest,
        context: WebsocketRequestContext,
    ): GetConversationResponse {
        return GetConversationResponse(getConversation(request.id))
    }
    
    fun getConversation(id: String): Conversation {
        val accessKeyId = System.getenv("AWS_ACCESS_KEY_ID")
        val secretKey = System.getenv("AWS_SECRET_ACCESS_KEY")
        val sessionToken = System.getenv("AWS_SESSION_TOKEN")
        
        val region = "us-west-2"
        val service = "dynamodb"
        val method = "POST"
        val url = "https://dynamodb.us-west-2.amazonaws.com"
        
        val table = "conversations"
        val request = GetItemRequest<Conversation>(
            tableName = table,
            key = buildDynamoDBObject {
                put("id", id)
            }
        )
        val body = configuration.encodeToString(GetItemRequest.serializer(Conversation.serializer()), request)
        
        val headers = listOf(
            AWSRequestSigner.Header("X-Amz-Security-Token", sessionToken),
            AWSRequestSigner.Header("X-Amz-Target", "DynamoDB_20120810.GetItem"),
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
        
        val response = client.newCall(call).execute().use { response ->
            val responseBody = response.body?.string() ?: throw Exception("Missing response body")
            configuration.decodeFromString(GetItemResponse.serializer(Conversation.serializer()), responseBody)
        }
        
        return response.item
    }
}
