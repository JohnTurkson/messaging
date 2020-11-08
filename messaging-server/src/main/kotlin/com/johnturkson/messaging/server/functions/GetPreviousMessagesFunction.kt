package com.johnturkson.messaging.server.functions

import com.johnturkson.awstools.dynamodb.objectbuilder.buildDynamoDBObject
import com.johnturkson.awstools.dynamodb.request.QueryRequest
import com.johnturkson.awstools.dynamodb.request.QueryResponse
import com.johnturkson.awstools.signer.AWSRequestSigner
import com.johnturkson.messaging.server.data.Message
import com.johnturkson.messaging.server.lambda.WebsocketLambdaFunction
import com.johnturkson.messaging.server.lambda.WebsocketRequestContext
import com.johnturkson.messaging.server.requests.GetPreviousMessagesRequest
import com.johnturkson.messaging.server.responses.GetPreviousMessagesResponse
import com.johnturkson.messaging.server.responses.Response
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.URL

class GetPreviousMessagesFunction : WebsocketLambdaFunction<GetPreviousMessagesRequest, GetPreviousMessagesResponse> {
    override val configuration = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
    override val inputSerializer = GetPreviousMessagesRequest.serializer()
    override val outputSerializer = Response.serializer()
    
    override fun processRequest(
        request: GetPreviousMessagesRequest,
        context: WebsocketRequestContext,
    ): GetPreviousMessagesResponse {
        return GetPreviousMessagesResponse(
            request.conversation,
            getPreviousMessages(request.conversation, request.lastMessage),
        )
    }
    
    fun getPreviousMessages(conversation: String, lastMessageId: String, limit: Int = 100): List<Message> {
        val accessKeyId = System.getenv("AWS_ACCESS_KEY_ID")
        val secretKey = System.getenv("AWS_SECRET_ACCESS_KEY")
        val sessionToken = System.getenv("AWS_SESSION_TOKEN")
        
        val region = "us-west-2"
        val service = "dynamodb"
        val method = "POST"
        val url = "https://dynamodb.us-west-2.amazonaws.com"
        
        val table = "messages"
        val lastMessage = GetMessageFunction().getMessage(lastMessageId)
        val request = QueryRequest<String>(
            tableName = table,
            indexName = "conversation",
            keyConditionExpression = "#conversation = :conversation",
            expressionAttributeNames = mapOf("#conversation" to "conversation"),
            expressionAttributeValues = buildDynamoDBObject {
                put(":conversation", conversation)
            },
            exclusiveStartKey = buildDynamoDBObject {
                put("id", lastMessageId)
                put("conversation", conversation)
                put("time", lastMessage.time)
            },
            limit = limit
        )
        val body = configuration.encodeToString(QueryRequest.serializer(String.serializer()), request)
        
        val headers = listOf(
            AWSRequestSigner.Header("X-Amz-Security-Token", sessionToken),
            AWSRequestSigner.Header("X-Amz-Target", "DynamoDB_20120810.Query"),
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
            val responseBody = response.body?.string() ?: throw Exception("Missing Query response body")
            configuration.decodeFromString(QueryResponse.serializer(Message.serializer()), responseBody)
        }
        
        return response.items
    }
}
