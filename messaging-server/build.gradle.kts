plugins {
    kotlin("jvm") version "1.4.10"
    kotlin("plugin.serialization") version "1.4.10"
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.0.0-RC")
    implementation("com.amazonaws:aws-lambda-java-core:1.2.1")
    implementation("com.squareup.okhttp3:okhttp:4.9.0")
    implementation("com.johnturkson.aws-tools:aws-dynamodb-object-builder:0.0.5")
    implementation("com.johnturkson.aws-tools:aws-dynamodb-request-builder:0.0.5")
    implementation("com.johnturkson.aws-tools:aws-dynamodb-transforming-serializer:0.0.5")
    implementation("com.johnturkson.aws-tools:aws-request-signer:0.0.5")
}

tasks.named<Jar>("jar") {
    configurations.runtimeClasspath.get().forEach { file -> from(zipTree(file.absoluteFile)) }
}
