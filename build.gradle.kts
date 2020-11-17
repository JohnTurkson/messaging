plugins {
    kotlin("jvm") version "1.4.10" apply false
    kotlin("plugin.serialization") version "1.4.10" apply false
    `maven-publish`
}

allprojects {
    group = "com.johnturkson.messaging"
    version = "0.0.1"
    
    repositories {
        mavenCentral()
        jcenter()
        maven("https://maven.pkg.jetbrains.space/johnturkson/p/packages/maven")
    }
}
