plugins {
    kotlin("multiplatform") version "1.6.10"
    kotlin("plugin.serialization") version "1.6.10"
}

group = "com.tairitsu"
version = "1.0-SNAPSHOT"

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

    run {
        sourceSets.create("demoMap") {
            kotlin.srcDir("src/main/kotlin")
        }
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

        val demoMap by getting {
            dependsOn(commonMain)
        }
    }
}
