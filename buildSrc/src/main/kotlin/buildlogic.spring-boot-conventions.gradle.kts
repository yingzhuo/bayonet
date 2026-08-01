import org.springframework.boot.gradle.tasks.bundling.BootBuildImage
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.springframework.boot")
}

springBoot {
    buildInfo {
        excludes = setOf("time")
    }
}

val excludeBouncyCastle = (project.findProperty("excludeBouncyCastle") as? String)?.toBoolean() ?: false


tasks.named<BootJar>("bootJar") {
    manifest {
        attributes("Main-Class" to "org.springframework.boot.loader.launch.PropertiesLauncher")
        attributes("Implementation-Title" to project.name)
        attributes("Implementation-Version" to project.version)
    }

    includeTools = true

    layered {
        enabled = false
    }

    if (excludeBouncyCastle) {
        exclude("**/bc*-jdk18on-*.jar", "mysql-connector-java*.jar")
    }
}

tasks.named<Jar>("jar") {
    enabled = false
}

tasks.named<BootBuildImage>("bootBuildImage") {
    enabled = false
}
