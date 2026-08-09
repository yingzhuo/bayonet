plugins {
    id("buildlogic.root-conventions")
}

description = "SpringBoot4.x增强库，提供可复用的自动配置、工具类和集成支持，涵盖 Web、安全、数据、校验等领域。"

allprojects {
    group = "com.github.yingzhuo"
    version = "4.1.1"

    configurations.configureEach {
        resolutionStrategy {
            cacheChangingModulesFor(7, "days")
            cacheDynamicVersionsFor(7, "days")
        }
    }
}
