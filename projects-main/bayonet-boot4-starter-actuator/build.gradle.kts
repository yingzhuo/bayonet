plugins {
    id("buildlogic.java-conventions")
    id("buildlogic.publish-conventions")
    id("buildlogic.code-counting-conventions")
}

description = "SpringBoot Actuator 增强"

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
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    // actuator
    api("org.springframework.boot:spring-boot-starter-actuator")

    // bayonet
    api(project(":projects-main:bayonet-boot4-starter-common"))

    // lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // slf4j
    api("org.slf4j:slf4j-api")
}
