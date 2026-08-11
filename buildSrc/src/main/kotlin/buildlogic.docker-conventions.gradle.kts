plugins {
    id("com.google.cloud.tools.jib")
}

jib {
    val dockerImage = (project.findProperty("dockerImage") as? String)
        ?: "bayonet/${project.name}:${project.version}"

    from {
        image =
            "docker.m.daocloud.io/eclipse-temurin:17-jre@sha256:e4f018a55645ad204892e44eb35437518d7e108ba2a2dce305024ab371d24876"
    }

    to {
        image = dockerImage
        tags = setOf("latest")
    }

    container {
        mainClass = project.findProperty("jibMainClass") as? String
    }
}
