plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.spring.boot.plugin)
    implementation(libs.dependency.management.plugin)
    implementation(libs.maven.publish.plugin)
    implementation(libs.git.properties.plugin)
    implementation(libs.jib.gradle.plugin)
}
