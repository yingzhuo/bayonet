plugins {
    id("base")
}

defaultTasks("clean", "classes")

ext {
}

description = "SpringBoot4.x增强库，提供可复用的自动配置、工具类和集成支持，涵盖 Web、安全、数据、校验等领域"

allprojects {
    group = project.property("bayonetGroup").toString()
    version = project.property("bayonetVersion").toString()

    configurations.configureEach {
        resolutionStrategy {
            cacheChangingModulesFor(7, "days")
            cacheDynamicVersionsFor(7, "days")
        }
    }
}

tasks.withType<Wrapper>().configureEach {
    distributionUrl =
        "https://mirrors.cloud.tencent.com/gradle/gradle-${project.property("gradleWrapperVersion")}-bin.zip"
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
            include("**/.DS_Store", "**/*.log")
        })
    }
}
