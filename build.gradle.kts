plugins {
    java
    `maven-publish`
    kotlin("jvm") version "1.9.0"
}

val publicationVersion = "1.0"
group = "com.azuyamat.kormmand"
version = "1.0"


allprojects {
    repositories {
        mavenCentral()
    }

    tasks.register("hello") {
        doLast {
            println("Hello from ${project.name}")
        }
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")

    dependencies {
        testImplementation ("io.github.cdimascio:dotenv-java:3.0.0")
        implementation ("org.jetbrains.kotlin:kotlin-reflect:1.9.10")
        if (project.name != "common") {
            implementation(project(":common"))
            testImplementation(project(":common"))
        }
    }

    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["java"])
                groupId = "com.azuyamat.kormmand"
                artifactId = project.name
                version = publicationVersion
            }
        }
    }
}