import org.springframework.boot.gradle.tasks.bundling.BootBuildImage
import org.springframework.boot.gradle.tasks.bundling.BootJar

val excludeBouncyCastle = (project.findProperty("excludeBouncyCastle") as? String)?.toBoolean() ?: false

plugins {
    id("org.springframework.boot")
    id("com.gorylenko.gradle-git-properties")
}

springBoot {
    buildInfo {
        excludes = setOf("time")
    }
}

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
        exclude("**/bc*-jdk18on-*.jar")
    }
}

tasks.named<Jar>("jar") {
    enabled = false
}

tasks.named<BootBuildImage>("bootBuildImage") {
    enabled = false
}

gitProperties {
    dotGitDirectory = rootProject.layout.projectDirectory.dir(".git/")
    gitPropertiesName = "git.properties"
    failOnNoGitDirectory = false
    keys = listOf(
        "git.branch", "git.commit.id", "git.commit.id.abbrev", "git.commit.time", "git.dirty"
    )
}
