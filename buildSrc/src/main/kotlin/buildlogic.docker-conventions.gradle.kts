val dockerImage = (project.findProperty("dockerImage") as? String)
    ?: "bayonet/${project.name}:${project.version}"

plugins {
    id("com.google.cloud.tools.jib")
}

jib {
    from {
        image = "docker.m.daocloud.io/eclipse-temurin:17-jre"
    }

    to {
        image = dockerImage
        // latest 始终跟随最后一次构建的镜像，版本 tag 的镜像可堆叠
        tags = setOf("latest")
    }

    container {
        mainClass = project.findProperty("jibMainClass") as? String
    }
}
