plugins {
    kotlin("jvm") version "1.4.10"
    kotlin("plugin.serialization") version "1.4.10"
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1")
    implementation("com.amazonaws:aws-lambda-java-core:1.2.1")
    implementation("io.ktor:ktor-client-cio:1.4.2")
    implementation("io.ktor:ktor-client-websockets:1.4.2")
    implementation("io.ktor:ktor-client-serialization:1.4.2")
    implementation("com.johnturkson.aws-tools:aws-dynamodb-object-builder:0.0.26")
    implementation("com.johnturkson.aws-tools:aws-dynamodb-request-builder:0.0.26")
    implementation("com.johnturkson.aws-tools:aws-dynamodb-transforming-serializer:0.0.26")
    implementation("com.johnturkson.aws-tools:aws-dynamodb-request-handler:0.0.26")
    implementation("com.johnturkson.aws-tools:aws-request-signer:0.0.26")
    implementation("com.johnturkson.aws-tools:aws-request-handler:0.0.26")
}

tasks.named<Jar>("jar") {
    configurations.runtimeClasspath.get().forEach { file -> from(zipTree(file.absoluteFile)) }
}
