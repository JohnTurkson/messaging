allprojects {
    group = "com.johnturkson.messaging"
    version = "0.0.1"
    
    repositories {
        mavenCentral()
        jcenter()
        
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/johnturkson/aws-tools")
            credentials {
                username = System.getenv("USERNAME")
                password = System.getenv("TOKEN")
            }
        }
    }
}
