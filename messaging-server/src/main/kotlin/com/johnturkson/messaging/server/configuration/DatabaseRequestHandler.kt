package com.johnturkson.messaging.server.configuration

import com.johnturkson.awstools.dynamodb.requesthandler.DynamoDBRequestHandler

object DatabaseRequestHandler {
    val instance: DynamoDBRequestHandler = DynamoDBRequestHandler(
        CredentialsConfiguration.instance,
        ClientConfiguration.instance,
        RegionConfiguration.instance,
        SerializerConfiguration.instance,
    )
}
