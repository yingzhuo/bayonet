plugins {
    id("base")
}

tasks.named<Wrapper>("wrapper") {
    val gradleWrapperVersion = findProperty("gradleWrapperVersion") as? String ?: "9.7.1"
    distributionUrl = "https://mirrors.cloud.tencent.com/gradle/gradle-$gradleWrapperVersion-bin.zip"
}
