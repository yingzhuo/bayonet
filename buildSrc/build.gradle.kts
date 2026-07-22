import java.util.*

plugins {
    `kotlin-dsl`
}

val propertiesFile = file("../gradle.properties")
if (propertiesFile.exists() && propertiesFile.isFile) {
    val props = Properties()
    propertiesFile.inputStream().use { stream ->
        props.load(stream)
    }
    props.forEach { (key, value) ->
        project.extensions.extraProperties.set(key.toString(), value.toString())
    }
}

dependencies {
    implementation(libs.dependency.management.plugin)
    implementation(libs.maven.publish.plugin)
}
