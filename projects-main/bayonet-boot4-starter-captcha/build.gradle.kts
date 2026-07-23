plugins {
    id("buildlogic.java-conventions")
    id("buildlogic.publish-conventions")
}

description = "图形验证码增强"

dependencies {
    compileOnly(libs.jetbrains.annotation)

    // test
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // spring-boot & spring
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    compileOnly("org.springframework.boot:spring-boot-autoconfigure")
    compileOnly("org.springframework.boot:spring-boot-configuration-processor")
    compileOnly("org.springframework.boot:spring-boot-autoconfigure-processor")
    compileOnly("org.springframework.boot:spring-boot-starter-data-redis") // optional
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    // bayonet
    api(project(":projects-main:bayonet-boot4-starter-common"))

    // easy-captcha
    api(libs.easy.captcha)

    // caffeine (optional, for CaffeineCaptchaManager)
    compileOnly("com.github.ben-manes.caffeine:caffeine")

    // lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // slf4j
    api("org.slf4j:slf4j-api")
}
