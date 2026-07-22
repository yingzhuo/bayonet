plugins {
    id("java-platform")
    id("buildlogic.publish-conventions")
}

description = "BOM"

javaPlatform {
    allowDependencies()
}

dependencies {
    constraints {
        // 除了 'bayonet-bom' 自身的产物都要纳入物料清单
        rootProject.findProject("projects-main")
            ?.subprojects
            ?.filter { it.name != "bayonet-bom" }
            ?.map { it.path }
            ?.forEach { api(project(path = it)) }

        api(libs.jetbrains.annotation)
        api(libs.java.jwt)
        api(libs.hocon)
        api(libs.bundles.zxing)

        // other bom
        api(libs.bc.bom)
        api(libs.ayza.bom)
    }
}
