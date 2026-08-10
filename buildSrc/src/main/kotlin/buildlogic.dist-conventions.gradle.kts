import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("distribution")
}

distributions {
    named("main") {
        distributionBaseName = project.name
        contents {
            from(tasks.named<BootJar>("bootJar")) {
                into("lib")
                rename { fileName ->
                    fileName.replace("-${project.version}", "")
                }
            }
            from("src/main/dist") {
            }
            from("$rootDir") {
                include("README.md")
            }
            duplicatesStrategy = DuplicatesStrategy.EXCLUDE
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
