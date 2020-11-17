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

subprojects {
    apply(plugin = "maven-publish")
    
    publishing {
        publications {
            create<MavenPublication>("maven") {
                pom {
                    name.set(project.name)
                    description.set("A messaging service written purely in Kotlin.")
                    url.set("https://www.github.com/JohnTurkson/messaging")
                    
                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }
                    
                    developers {
                        developer {
                            id.set("JohnTurkson")
                            name.set("John Turkson")
                            email.set("johnturkson@johnturkson.com")
                            url.set("https://www.johnturkson.com")
                        }
                    }
                    
                    scm {
                        connection.set("scm:git:git://github.com/JohnTurkson/messaging.git")
                        developerConnection.set("scm:git:ssh://git.jetbrains.space/johnturkson/messaging/messaging.git")
                        url.set("https://github.com/JohnTurkson/messaging")
                    }
                }
                
                afterEvaluate {
                    from(components["kotlin"])
                }
            }
        }
        
        repositories {
            maven {
                name = "SpacePackages"
                url = uri("https://maven.pkg.jetbrains.space/johnturkson/p/packages/public")
                credentials {
                    username = System.getenv("SPACE_PUBLISHING_USERNAME")
                    password = System.getenv("SPACE_PUBLISHING_TOKEN")
                }
            }
            
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/johnturkson/messaging")
                credentials {
                    username = System.getenv("GITHUB_PUBLISHING_USERNAME")
                    password = System.getenv("GITHUB_PUBLISHING_TOKEN")
                }
            }
        }
    }
}
