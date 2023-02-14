import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    `maven-publish`
    kotlin("jvm") version "1.5.31"
    id("com.gradle.plugin-publish") version "1.1.0"
}

repositories {
    mavenCentral()
    google()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.android.tools.build:gradle:7.4.0")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

group = "com.keyri"
version = System.getenv("GRADLE_PUBLISH_VERSION")

gradlePlugin {
    plugins {
        create("KeyriChecksumCheckerPlugin") {
            id = "com.keyri.checksumchecker.plugin"
            displayName = "Keyri Checksum Checker Plugin"
            displayName =
                "Gradle plugin for Android project to get and upload APK files checksums on Keyri. This should increase code tampering protection."
            implementationClass = "com.keyri.checksumchecker.plugin.KeyriChecksumCheckerPlugin"
        }
    }
}

pluginBundle {
    website = "https://github.com/Keyri-Co/keyri-checksum-checker-plugin"
    vcsUrl = "https://github.com/Keyri-Co/keyri-checksum-checker-plugin.git"
    tags = listOf("Android", "APK", "Kotlin", "Security")
}
