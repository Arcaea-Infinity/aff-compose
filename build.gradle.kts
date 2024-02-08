plugins {
    alias(libs.plugins.jvm)
    kotlin("plugin.serialization") version "1.9.21"
    `java-library`
    java
    `maven-publish`
    id("com.autonomousapps.dependency-analysis") version "1.29.0"
}

group = "com.tairitsu"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    testImplementation(libs.junit.jupiter.engine)
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    api("org.jetbrains.kotlinx:kotlinx-serialization-json:${project.property("kotlinxSerializationJsonVersion")}")
    api("org.jetbrains.kotlinx:kotlinx-serialization-core:${project.property("kotlinxSerializationJsonVersion")}")

    // https://mvnrepository.com/artifact/com.benasher44/uuid
    api("com.benasher44:uuid:0.8.2")

    // https://mvnrepository.com/artifact/io.ktor/ktor-io
    api("io.ktor:ktor-io:2.3.8")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            groupId = project.group.toString()
            artifactId = "aff-compose"
            version = project.version.toString()
        }
    }
}