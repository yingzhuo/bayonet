import org.apache.tools.ant.filters.ReplaceTokens
import org.springframework.boot.gradle.plugin.SpringBootPlugin
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

plugins {
    id("java")
    id("java-library")
    id("io.spring.dependency-management")
}

val jdkVersion: Int = 17

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
        mavenBom(SpringBootPlugin.BOM_COORDINATES)
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.addAll(
        listOf(
            "-parameters",
            "-Werror",
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



tasks.named<ProcessResources>("processResources") {
    // 配置文件替换token
    val tokens = mapOf(
        "APP_GROUP" to project.group,
        "APP_NAME" to project.name,
        "APP_VERSION" to project.version,
        "APP_GRADLE_VERSION" to project.gradle.gradleVersion,
        "APP_BUILD_TIMESTAMP" to DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS").format(LocalDateTime.now())
    )

    from(rootDir) {
        include("LICENSE*", "NOTICE*")
        into("META-INF")
    }

    // 对 resources 中需要 token 替换的文件应用过滤（不再重复添加 src/main/resources source）
    listOf(
        "**/*.yaml",
        "**/*.yml",
        "**/*.properties",
        "**/*.conf",
        "**/*.toml",
        "**/banner.txt"
    ).forEach { pattern ->
        filesMatching(pattern) {
            filter<ReplaceTokens>("tokens" to tokens)
        }
    }
    filteringCharset = "UTF-8"
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    exclude("**/.DS_Store", "**/.gitkeep", ".gitignore")
}

tasks.named<Copy>("processTestResources") {
    exclude("**/.DS_Store", "**/.gitkeep", ".gitignore")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
