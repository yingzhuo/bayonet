plugins {
    id("base")
}

val gradleWrapperVersion = findProperty("gradleWrapperVersion") as? String ?: "9.7.0"

tasks.named<Wrapper>("wrapper") {
    distributionUrl = "https://mirrors.cloud.tencent.com/gradle/gradle-$gradleWrapperVersion-bin.zip"
}
