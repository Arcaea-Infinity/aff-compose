plugins {
    alias(libs.plugins.jvm)
    kotlin("plugin.serialization") version "1.9.21"
    `java-library`
    java
    `maven-publish`
}

group = "com.tairitsu"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation(libs.junit.jupiter.engine)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    api(libs.commons.math3)

    // https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-datetime
    api("org.jetbrains.kotlinx:kotlinx-datetime:0.5.0")

    // https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-datetime
    api("org.jetbrains.kotlinx:kotlinx-datetime:0.5.0")

    // https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-serialization-json
    api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")

    // https://mvnrepository.com/artifact/com.benasher44/uuid
    api("com.benasher44:uuid:0.8.2")

    // https://mvnrepository.com/artifact/io.ktor/ktor-io
    api("io.ktor:ktor-io:2.3.8")

    implementation(libs.guava)
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