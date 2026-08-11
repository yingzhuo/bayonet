plugins {
    id("com.google.cloud.tools.jib")
}

jib {
    val dockerImage = findProperty("dockerImage") as? String ?: "bayonet/${project.name}:${project.version}"
    val baseDockerImage = findProperty("baseDockerImage") as? String
        ?: "docker.m.daocloud.io/eclipse-temurin:17-jre@sha256:e4f018a55645ad204892e44eb35437518d7e108ba2a2dce305024ab371d24876"

    from {
        image = baseDockerImage
    }

    to {
        image = dockerImage
        tags = setOf("latest")
    }

    container {
        mainClass = findProperty("jibMainClass") as? String
    }
}
