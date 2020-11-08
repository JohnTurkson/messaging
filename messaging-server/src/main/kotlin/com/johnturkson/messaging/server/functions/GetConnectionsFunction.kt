package com.johnturkson.messaging.server.functions

import com.johnturkson.awstools.dynamodb.request.ScanRequest
import com.johnturkson.awstools.dynamodb.request.ScanResponse
import com.johnturkson.awstools.signer.AWSRequestSigner.Header
import com.johnturkson.awstools.signer.AWSRequestSigner.generateRequestHeaders
import com.johnturkson.messaging.server.data.Connection
import com.johnturkson.messaging.server.lambda.WebsocketLambdaFunction
import com.johnturkson.messaging.server.lambda.WebsocketRequestContext
import com.johnturkson.messaging.server.requests.GetConnectionsRequest
import com.johnturkson.messaging.server.responses.GetConnectionsResponse
import com.johnturkson.messaging.server.responses.Response
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.URL

class GetConnectionsFunction : WebsocketLambdaFunction<GetConnectionsRequest, GetConnectionsResponse> {
    override val configuration = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
    override val inputSerializer = GetConnectionsRequest.serializer()
    override val outputSerializer = Response.serializer()
    
    override fun processRequest(
        request: GetConnectionsRequest,
        context: WebsocketRequestContext,
    ): GetConnectionsResponse {
        return GetConnectionsResponse(getConnections())
    }
    
    fun getConnections(): List<Connection> {
        val accessKeyId = System.getenv("AWS_ACCESS_KEY_ID")
        val secretKey = System.getenv("AWS_SECRET_ACCESS_KEY")
        val sessionToken = System.getenv("AWS_SESSION_TOKEN")
        
        val region = "us-west-2"
        val service = "dynamodb"
        val method = "POST"
        val url = "https://dynamodb.us-west-2.amazonaws.com"
        
        val table = "connections"
        val request = ScanRequest<String>(table)
        val body = configuration.encodeToString(ScanRequest.serializer(String.serializer()), request)
        
        val headers = listOf(
            Header("X-Amz-Security-Token", sessionToken),
            Header("X-Amz-Target", "DynamoDB_20120810.Scan"),
        )
        
        val signedHeaders = generateRequestHeaders(
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
            val responseBody = response.body?.string() ?: throw Exception("Missing Scan response body")
            configuration.decodeFromString(ScanResponse.serializer(Connection.serializer()), responseBody)
        }
        
        return response.items
    }
}
