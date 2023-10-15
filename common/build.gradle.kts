plugins {
    application
}

group = "com.azuyamat.kormmand"
version = "1.0.0"

application {
    mainClass.set("Manager")
}

tasks {
    distZip {
        dependsOn("shadowJar")
    }

    distTar {
        dependsOn("shadowJar")
    }

    startScripts {
        dependsOn("shadowJar")
    }
}