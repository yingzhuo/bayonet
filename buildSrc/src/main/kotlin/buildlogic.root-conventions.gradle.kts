val gradleWrapperVersion: String = project.property("gradleWrapperVersion").toString()

plugins {
    id("base")
}

tasks.withType<Wrapper>().configureEach {
    distributionUrl = "https://mirrors.cloud.tencent.com/gradle/gradle-${gradleWrapperVersion}-bin.zip"
    networkTimeout = 30000
    distributionType = Wrapper.DistributionType.ALL
}
