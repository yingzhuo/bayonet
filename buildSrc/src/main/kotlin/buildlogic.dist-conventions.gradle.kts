plugins {
    id("distribution")
}

distributions {
    named("main") {
        distributionBaseName = project.name

        contents {

            // spring-boot uberjar
            from(tasks.named("bootJar")) {
                include("**/*.jar")
                into("lib")
                rename {
                    it.replace("-${project.version}", "")
                }
            }

            // 其他配置与脚本
            from("src/main/dist")

            // 重复文件逻辑
            duplicatesStrategy = DuplicatesStrategy.INCLUDE
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
