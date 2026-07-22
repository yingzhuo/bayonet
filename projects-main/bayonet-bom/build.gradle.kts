plugins {
    id("java-platform")
    id("buildlogic.publish-conventions")
}

description = "BOM"

javaPlatform {
    allowDependencies()
}

val coreSubprojectPaths = rootProject.findProject("projects-main")
    ?.subprojects
    ?.filter { it.name != "bayonet-bom" }
    ?.map { it.path } ?: emptyList()

dependencies {
    constraints {
        coreSubprojectPaths.forEach { path ->
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
