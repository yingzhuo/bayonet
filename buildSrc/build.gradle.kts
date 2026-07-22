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

val dependencyManagementPluginVersion: String = project.property("dependencyManagementPluginVersion").toString()
val mavenPublishVersion: String = project.property("mavenPublishVersion").toString()

dependencies {
    implementation("io.spring.dependency-management:io.spring.dependency-management.gradle.plugin:$dependencyManagementPluginVersion")
    implementation("com.vanniktech.maven.publish:com.vanniktech.maven.publish.gradle.plugin:$mavenPublishVersion")
}
