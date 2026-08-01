plugins {
    id("java-platform")
    id("buildlogic.publish-conventions")
    id("buildlogic.code-counting-conventions")
}

description = "BOM"

javaPlatform {
    allowDependencies()
}

dependencies {
    constraints {
        rootProject.findProject("projects-main")
            ?.subprojects
            ?.filter { it.name != "bayonet-bom" }
            ?.map { it.path }
            ?.forEach { api(project(path = it)) }

        api(libs.jetbrains.annotation)
        api(libs.java.jwt)
        api(libs.hocon)
        api(libs.bundles.zxing)
        api(libs.easy.captcha)

        // other bom
        api(libs.bc.bom)
    }
}
