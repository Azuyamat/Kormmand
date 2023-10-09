plugins {
    kotlin("jvm") version "1.9.0"
    application
    id("maven-publish")
}

group = "com.azuyamat"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    implementation("dev.kord:kord-core:0.11.1")
    implementation("io.github.cdimascio:dotenv-java:3.0.0")
    implementation("org.mongodb:mongodb-driver-kotlin-sync:4.10.2")
    implementation("org.slf4j:slf4j-nop:2.0.9")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}

application {
    mainClass.set("MainKt")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            groupId = "com.azuyamat"
            artifactId = "kormmand"
            version = "1.0.0"

            pom {
                name.set("Kormmand")
                description.set("Lightweight Kord command manager")
            }
        }
    }
    repositories {
        maven {
            name = "jitpack"
            url = uri("https://jitpack.io")
        }
    }
}