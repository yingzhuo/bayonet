val bayonetVersion: String = project.property("bayonetVersion").toString()

plugins {
    id("com.vanniktech.maven.publish")
}

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()

    pom {
        name = project.name
        description = project.description ?: "Another enhancement library of SpringBoot & SpringFramework"
        url = "https://github.com/yingzhuo/bayonet"
        inceptionYear = "2026"

        licenses {
            license {
                name = "Apache License, Version 2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
            }
        }

        developers {
            developer {
                id = "yingzhuo"
                name = "应卓"
                email = "yingzhor@gmail.com"
                url = "https://github.com/yingzhuo"
                roles = listOf("author")
                timezone = "+8"
            }
        }

        scm {
            url = "git@github.com:yingzhuo/bayonet.git"
            connection = "scm:git:git@github.com:yingzhuo/bayonet.git"
            developerConnection = "scm:git:git@github.com:yingzhuo/bayonet.git"
        }

        issueManagement {
            system = "GitHub Issues"
            url = "https://github.com/yingzhuo/bayonet/issues"
        }
    }
}

tasks.named("publishAllPublicationsToMavenCentralRepository") {
    doFirst {
        if (project.version.toString().endsWith("-SNAPSHOT")) {
            throw GradleException(
                "Cannot publish SNAPSHOT version '$bayonetVersion' to Maven Central. " +
                    "Release a non-SNAPSHOT version first."
            )
        }
    }
}
