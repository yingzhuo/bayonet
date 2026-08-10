import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("distribution")
}

distributions {
    named("main") {
        distributionBaseName = project.name
        contents {
            duplicatesStrategy = DuplicatesStrategy.EXCLUDE

            // spring-boot uberjar
            from(tasks.named<BootJar>("bootJar")) {
                include("**/*.jar")
                into("lib")
                rename { fileName ->
                    fileName.replace("-${project.version}", "")
                }
            }

            // 其他配置与脚本
            from("src/main/dist")

            // README.md
            from(rootDir) {
                include("README.md")
            }
        }
    }
}

tasks.named<Zip>("distZip") {
    enabled = false
}

tasks.named<Tar>("distTar") {
    enabled = true
    compression = Compression.GZIP
    archiveExtension = "tgz"
}
