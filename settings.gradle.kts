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

// 集成测试 (临时性的)
include(":project-integration-test")

// 实际产物
includeSubmodules("projects-main")

// ------
fun includeSubmodules(baseDir: String) {
    file(baseDir).listFiles()
        ?.filter { it.isDirectory && file("${it.path}/build.gradle.kts").exists() }
        ?.sortedBy { it.name }
        ?.forEach { dir ->
            include(":$baseDir:${dir.name}")
        }
}
