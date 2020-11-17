job("Publish artifacts") {
    container("openjdk:11") {
        env["SPACE_PUBLISHING_USERNAME"] = Secrets("space-publishing-username")
        env["SPACE_PUBLISHING_TOKEN"] = Secrets("space-publishing-token")
        env["GITHUB_PUBLISHING_USERNAME"] = Secrets("github-publishing-username")
        env["GITHUB_PUBLISHING_TOKEN"] = Secrets("github-publishing-token")
        
        kotlinScript { api ->
            if (api.gitBranch() == "refs/heads/releases") {
                api.gradlew("build")
                api.gradlew("publish")
            }
        }
    }
}
