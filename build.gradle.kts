plugins {
    java
    `maven-publish`
    kotlin("jvm") version "1.9.0"
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

val publicationVersion = "1.0"
group = "com.azuyamat.kormmand"
version = "1.0"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

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
    apply(plugin = "com.github.johnrengelman.shadow")
    apply(plugin = "kotlin")

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
                groupId = "com.azuyamat.kormmand"
                artifactId = project.name
                version = publicationVersion

                from(components["java"])
            }
        }
    }

    tasks {
        compileKotlin {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }
}