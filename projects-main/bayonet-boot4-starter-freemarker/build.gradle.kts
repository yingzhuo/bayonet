plugins {
    id("buildlogic.java-conventions")
    id("buildlogic.publish-conventions")
}

description = "FreeMarker模板引擎增强"

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

    // bayonet
    api(project(":projects-main:bayonet-boot4-starter-common"))

    // freemarker
    api(libs.freemarker)

    // lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // slf4j
    api("org.slf4j:slf4j-api")
}
