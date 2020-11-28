package com.johnturkson.messaging.server.configuration

import com.johnturkson.awstools.ses.requesthandler.SESRequestHandler

object EmailRequestHandler {
    val instance = SESRequestHandler(
        CredentialsConfiguration.instance,
        ClientConfiguration.instance,
        RegionConfiguration.instance,
        SerializerConfiguration.instance
    )
}
