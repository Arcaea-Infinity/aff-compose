plugins {
    kotlin("multiplatform") version "1.6.10"
    kotlin("plugin.serialization") version "1.6.10"
    id("java")
    id("java-library")
    id("maven-publish")
}

group = "com.tairitsu"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        withJava()
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    js(BOTH) {
        browser {
            commonWebpackConfig {
                cssSupport.enabled = true
            }
        }
    }
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                // https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-datetime
                api("org.jetbrains.kotlinx:kotlinx-datetime:0.3.2")
                // https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-datetime
                api("org.jetbrains.kotlinx:kotlinx-datetime:0.3.2")
                // https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-serialization-json
                api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
                // https://mvnrepository.com/artifact/com.benasher44/uuid
                api("com.benasher44:uuid:0.4.0")
                // https://mvnrepository.com/artifact/io.ktor/ktor-io
                api("io.ktor:ktor-io:2.0.0-beta-1")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting
        val jvmTest by getting
        val jsMain by getting
        val jsTest by getting
        val nativeMain by getting
        val nativeTest by getting
    }

    val publicationsFromMainHost =
        listOf(jvm(), js()).map { it.name } + "kotlinMultiplatform"

    project.ext.set("POM_DESCRIPTION", "Arcaea Aff composing DSL")
}

apply { from(project.file("$rootDir/gradle/publish-helper.gradle.kts")) }
