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
include(":projects-main:bayonet-bom")
include(":projects-main:bayonet-boot4-starter-common")
include(":projects-main:bayonet-boot4-starter-freemarker")
include(":projects-main:bayonet-boot4-starter-hocon")
include(":projects-main:bayonet-boot4-starter-jdbc")
include(":projects-main:bayonet-boot4-starter-jwt")
include(":projects-main:bayonet-boot4-starter-security")
include(":projects-main:bayonet-boot4-starter-validation")
include(":projects-main:bayonet-boot4-starter-webcli")
include(":projects-main:bayonet-boot4-starter-webmvc")
include(":projects-main:bayonet-boot4-starter-zxing")
