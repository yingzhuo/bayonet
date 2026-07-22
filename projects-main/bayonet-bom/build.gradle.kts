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
        val subProjectPathLs = rootProject.findProject("projects-main")
            ?.subprojects
            ?.filter { it.name != "bayonet-bom" }
            ?.map { it.path } ?: emptyList()

        subProjectPathLs.forEach { path ->
            api(project(path = path))
        }

        api(libs.jetbrains.annotation)
        api(libs.java.jwt)
        api(libs.hocon)
        api(libs.bundles.zxing)

        // other bom
        api(libs.bc.bom)
        api(libs.ayza.bom)
    }
}
