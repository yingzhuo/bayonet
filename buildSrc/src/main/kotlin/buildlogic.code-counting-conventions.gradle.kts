import org.cthing.gradle.plugins.locc.LoccTask

plugins {
    id("org.cthing.locc")
}

locc {
    includeTestSources.set(false)
}

tasks.withType<LoccTask>().configureEach {
    reports {
        xml.required.set(false)
        html.required.set(true)
        json.required.set(false)
    }
}
