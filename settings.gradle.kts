pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        maven(url = "https://maven.aliyun.com/repository/gradle-plugin")
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenLocal()
        maven(url = "https://maven.aliyun.com/repository/public/")
        mavenCentral()
        maven(url = "https://repo.spring.io/release")
        maven(url = "https://repo.spring.io/milestone")
        maven(url = "https://repo.spring.io/snapshot")
    }
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
}

rootProject.name = "bayonet"

file("projects-main").listFiles()?.filter { it.isDirectory }?.forEach { projectDir ->
    if (file("${projectDir.path}/build.gradle.kts").exists()) {
        include(":projects-main:${projectDir.name}")
    }
}

include(":project-integration-test")
