val jdkVersion: Int = project.property("jdkVersion").toString().toInt()
val springBootVersion: String = project.property("springBootVersion").toString()

plugins {
    id("java")
    id("java-library")
    id("io.spring.dependency-management")
}

java {
    sourceCompatibility = JavaVersion.toVersion(jdkVersion)
    targetCompatibility = JavaVersion.toVersion(jdkVersion)

    toolchain {
        languageVersion = JavaLanguageVersion.of(jdkVersion)
        vendor = JvmVendorSpec.ORACLE
        implementation = JvmImplementation.VENDOR_SPECIFIC
    }
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:$springBootVersion")
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.addAll(
        listOf(
            "-parameters",
            "-Xlint:unchecked",
            "-Xlint:cast",
            "-Xlint:rawtypes",
            "-Xlint:overloads",
            "-Xlint:divzero",
            "-Xlint:finally",
            "-Xlint:static",
        )
    )
}

tasks.named<Jar>("jar") {
    manifest {
        attributes(
            "Implementation-Title" to project.name,
            "Implementation-Version" to project.version,
            "Implementation-Vendor" to "应卓",
            "Built-Jdk" to jdkVersion,
            "Created-By" to "Gradle ${gradle.gradleVersion}",
            "Url" to "https://github.com/yingzhuo/bayonet",
        )
    }
}

tasks.named<Javadoc>("javadoc") {
    isFailOnError = false

    options {
        this as StandardJavadocDocletOptions
        locale("zh_CN")
        encoding("UTF-8")
        addBooleanOption("html5", true)
        addBooleanOption("Xdoclint:none", true)
    }
}

tasks.named<Copy>("processResources") {
    from(rootDir) {
        include("LICENSE*", "NOTICE*")
        into("META-INF")
    }
    exclude("**/.DS_Store", "**/.gitkeep", ".gitignore")
}

tasks.named<Copy>("processTestResources") {
    exclude("**/.DS_Store", "**/.gitkeep", ".gitignore")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
