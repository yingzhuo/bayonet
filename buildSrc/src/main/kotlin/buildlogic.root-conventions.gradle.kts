plugins {
    id("base")
}

tasks.named<Wrapper>("wrapper") {
    distributionUrl = "https://mirrors.cloud.tencent.com/gradle/gradle-9.7.0-bin.zip"
}
