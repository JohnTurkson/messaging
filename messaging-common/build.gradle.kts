plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1")
}

tasks.named<Jar>("jar") {
    configurations.runtimeClasspath.get().forEach { file -> from(zipTree(file.absoluteFile)) }
}
