plugins {
    id("base")
}

defaultTasks("clean", "classes")

tasks.withType<Wrapper>().configureEach {
    distributionUrl = "https://mirrors.cloud.tencent.com/gradle/gradle-${project.property("gradleWrapperVersion")}-bin.zip"
    networkTimeout = 30000
    distributionType = Wrapper.DistributionType.ALL
}

tasks.named("clean") {
    finalizedBy("tidy")
}

tasks.register<Delete>("tidy") {
    description = "Delete useless files."
    group = LifecycleBasePlugin.BUILD_GROUP

    doLast {
        delete(fileTree(rootDir) {
            include("**/.DS_Store")
        })
    }
}
