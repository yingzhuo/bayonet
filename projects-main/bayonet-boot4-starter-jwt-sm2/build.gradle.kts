plugins {
    id("buildlogic.java-conventions")
    id("buildlogic.publish-conventions")
}

description = "JWT增强 - SM2加密"

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
    api(project(":projects-main:bayonet-boot4-starter-jwt"))

    // hutool
    api(platform(libs.hutool.bom))
    api("cn.hutool:hutool-crypto")

    // BC
    api(platform(libs.bc.bom))
    api("org.bouncycastle:bcprov-jdk18on")
    api("org.bouncycastle:bcpkix-jdk18on")

    // lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // slf4j
    api("org.slf4j:slf4j-api")
}
