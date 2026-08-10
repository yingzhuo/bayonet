import org.springframework.boot.gradle.tasks.bundling.BootBuildImage
import org.springframework.boot.gradle.tasks.bundling.BootJar
import org.springframework.boot.gradle.tasks.run.BootRun

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
    val buildExcludeBouncyCastle = (project.findProperty("buildExcludeBouncyCastle") as? String)?.toBoolean() ?: false

    manifest {
        attributes("Main-Class" to "org.springframework.boot.loader.launch.PropertiesLauncher")
        attributes("Implementation-Title" to project.name)
        attributes("Implementation-Version" to project.version)
    }

    includeTools = true

    layered {
        enabled = false
    }

    exclude(
        "**/.DS_Store",
        "**/.gitkeep",
        "**/netty-*-macos*.jar",
        "**/netty-*-osx*.jar"
    )

    if (buildExcludeBouncyCastle) {
        exclude("**/bc*-jdk18on-*.jar")
    }
}

tasks.named<Jar>("jar") {
    enabled = false
}

tasks.named<BootBuildImage>("bootBuildImage") {
    enabled = false // 不允许用 spring-boot 插件构建docker镜像
}

tasks.named<BootRun>("bootRun") {
    val bootRunSpringProfiles = (project.findProperty("bootRunSpringProfiles") as? String) ?: "dev"
    args("--spring.profiles.active=$bootRunSpringProfiles")
}

gitProperties {
    dotGitDirectory = rootProject.layout.projectDirectory.dir(".git/")
    gitPropertiesName = "git.properties"
    failOnNoGitDirectory = false
    keys = listOf(
        "git.branch", "git.commit.id", "git.commit.id.abbrev", "git.commit.time", "git.dirty"
    )
}
