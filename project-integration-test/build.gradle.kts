plugins {
    id("buildlogic.java-conventions")
    id("buildlogic.spring-boot-conventions")
}

description = "集成测试 (临时)"

configurations.configureEach {
}

dependencies {
    // spring-boot
    api("org.springframework.boot:spring-boot-starter-web")
    api("org.springframework.boot:spring-boot-starter-actuator")
    api("org.springframework.boot:spring-boot-starter-security")
    api("org.springframework.boot:spring-boot-http-client")

    // bayonet
    api(platform(project(":projects-main:bayonet-bom")))
    api(project(":projects-main:bayonet-boot4-starter-common"))
    api(project(":projects-main:bayonet-boot4-starter-hocon"))
    api(project(":projects-main:bayonet-boot4-starter-security"))
    api(project(":projects-main:bayonet-boot4-starter-jwt"))

    // lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // slf4j
    api("org.slf4j:slf4j-api")

    // BC
    api(platform(libs.bc.bom))
    api("org.bouncycastle:bcprov-jdk18on")
    api("org.bouncycastle:bcpkix-jdk18on")
}
