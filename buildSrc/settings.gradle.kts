pluginManagement {
    repositories {
        gradlePluginPortal()
        maven(url = "https://maven.aliyun.com/repository/gradle-plugin")
        mavenCentral()
        google()
    }
}

dependencyResolutionManagement {

    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }

    repositories {
        mavenLocal()
        maven(url = "https://maven.aliyun.com/repository/public/")
        mavenCentral()
        gradlePluginPortal() // 有些插件根本不发布到Maven中央仓库
        google()
    }

    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
}
