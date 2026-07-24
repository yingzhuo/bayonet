val gradleWrapperVersion: String = project.property("gradleWrapperVersion").toString()

plugins {
    id("base")
}

defaultTasks("classes")

allprojects {
    group = project.property("bayonetGroup").toString()
    version = project.property("bayonetVersion").toString()

    configurations.configureEach {
        resolutionStrategy {
            cacheChangingModulesFor(72, "hours")
            cacheDynamicVersionsFor(72, "hours")
        }
    }
}

tasks.withType<Wrapper>().configureEach {
    distributionUrl = "https://mirrors.cloud.tencent.com/gradle/gradle-$gradleWrapperVersion-bin.zip"
    networkTimeout = 30000
    distributionType = Wrapper.DistributionType.ALL
}
