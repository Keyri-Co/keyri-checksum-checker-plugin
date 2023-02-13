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

tasks.withType(JavaCompile::class.java).configureEach {
    sourceCompatibility = JavaVersion.VERSION_11.toString()
    targetCompatibility = JavaVersion.VERSION_11.toString()
}

group = "com.keyri"
version = "0.0.1"

gradlePlugin {
    plugins {
        create("KeyriChecksumCheckerPlugin") {
            id = "com.keyri.checksumchecker.plugin"
            displayName = "Keyri Checksum Checker Plugin"
            displayName = "Gradle plugin for Android project to get and upload APK files checksums on Keyri. This improves your app code tampering protection."
            implementationClass = "com.keyri.checksumchecker.plugin.KeyriChecksumCheckerPlugin"
        }
    }
}

pluginBundle {
    website = "https://github.com/Keyri-Co/keyri-checksum-checker-plugin"
    vcsUrl = "https://github.com/Keyri-Co/keyri-checksum-checker-plugin.git"
    tags = listOf("Android", "APK", "Kotlin", "Security")
}
