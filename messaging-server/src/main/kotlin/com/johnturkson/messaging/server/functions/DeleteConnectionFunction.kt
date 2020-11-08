package com.johnturkson.messaging.server.functions

import com.johnturkson.awstools.dynamodb.objectbuilder.buildDynamoDBObject
import com.johnturkson.awstools.dynamodb.request.DeleteItemRequest
import com.johnturkson.awstools.signer.AWSRequestSigner
import com.johnturkson.awstools.signer.AWSRequestSigner.Header
import com.johnturkson.messaging.server.data.Connection
import com.johnturkson.messaging.server.lambda.WebsocketLambdaFunction
import com.johnturkson.messaging.server.lambda.WebsocketRequestContext
import com.johnturkson.messaging.server.requests.DeleteConnectionRequest
import com.johnturkson.messaging.server.responses.DeleteConnectionResponse
import com.johnturkson.messaging.server.responses.Response
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.URL

class DeleteConnectionFunction : WebsocketLambdaFunction<DeleteConnectionRequest, DeleteConnectionResponse> {
    override val configuration = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
    override val inputSerializer = DeleteConnectionRequest.serializer()
    override val outputSerializer = Response.serializer()
    
    override fun processRequest(
        request: DeleteConnectionRequest,
        context: WebsocketRequestContext,
    ): DeleteConnectionResponse {
        // TODO only pass in context.connectionId
        deleteConnection(Connection(context.connectionId, request.data.user))
        return DeleteConnectionResponse
    }
    
    private fun deleteConnection(connection: Connection) {
        val accessKeyId = System.getenv("AWS_ACCESS_KEY_ID")
        val secretKey = System.getenv("AWS_SECRET_ACCESS_KEY")
        val sessionToken = System.getenv("AWS_SESSION_TOKEN")
        
        val region = "us-west-2"
        val service = "dynamodb"
        val method = "POST"
        val url = "https://dynamodb.us-west-2.amazonaws.com"
        
        val table = "connections"
        val request = DeleteItemRequest<String>(table, buildDynamoDBObject {
            put("id", connection.id)
        })
        val body = configuration.encodeToString(DeleteItemRequest.serializer(String.serializer()), request)
        
        val headers = listOf(
            Header("X-Amz-Security-Token", sessionToken),
            Header("X-Amz-Target", "DynamoDB_20120810.DeleteItem"),
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
    }
}
