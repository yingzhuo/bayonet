plugins {
    id("buildlogic.java-conventions")
    id("buildlogic.spring-boot-conventions")
    id("buildlogic.docker-conventions")
    id("buildlogic.dist-conventions")
}

description = "集成测试"

dependencies {
    // spring-boot
    api("org.springframework.boot:spring-boot-starter-web")
    api("org.springframework.boot:spring-boot-starter-actuator")
    api("org.springframework.boot:spring-boot-starter-security")

    // spring-framework
    compileOnly("org.springframework:spring-context-indexer")
    annotationProcessor("org.springframework:spring-context-indexer")

    // bayonet
    api(platform(project(":projects-main:bayonet-bom")))
    api(project(":projects-main:bayonet-boot4-starter-common"))
    api(project(":projects-main:bayonet-boot4-starter-actuator"))
    api(project(":projects-main:bayonet-boot4-starter-config-hocon"))
    api(project(":projects-main:bayonet-boot4-starter-jwt"))
    api(project(":projects-main:bayonet-boot4-starter-security"))
    api(project(":projects-main:bayonet-boot4-starter-validation"))

    // lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // slf4j
    api("org.slf4j:slf4j-api")

    // BC
    api(platform(libs.bc.bom))
    api("org.bouncycastle:bcprov-jdk18on")
}
