plugins {
    id("buildlogic.java-conventions")
    id("buildlogic.publish-conventions")
}

description = "WebClient增强"

dependencies {
    compileOnly(libs.jetbrains.annotation)

    // test
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // spring-boot & spring
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    api("org.springframework.boot:spring-boot-http-client")
    compileOnly("org.springframework.boot:spring-boot-autoconfigure")
    compileOnly("org.springframework.boot:spring-boot-configuration-processor")
    compileOnly("org.springframework.boot:spring-boot-autoconfigure-processor")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    // bayonet
    api(project(":projects-main:bayonet-boot4-starter-common"))

    // ayza (SSL Context tool)
    api(platform(libs.ayza.bom))
    compileOnly("io.github.hakky54:ayza")
    compileOnly("io.github.hakky54:ayza-for-pem")
    compileOnly("io.github.hakky54:ayza-for-apache5")

    // apache5
    compileOnly("org.apache.httpcomponents.client5:httpclient5")

    // lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // slf4j
    api("org.slf4j:slf4j-api")
}
