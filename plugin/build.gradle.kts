plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    id("com.gradle.plugin-publish") version "1.2.1"
}

repositories {
    mavenCentral()
    google()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.google.code.gson:gson:2.10.1")
}

kotlin {
    jvmToolchain {
        version = JavaVersion.VERSION_17
    }
}

group = "com.keyri"
version = System.getenv("GRADLE_PUBLISH_VERSION")

gradlePlugin {
    website.set("https://github.com/Keyri-Co/keyri-checksum-checker-plugin")
    vcsUrl.set("https://github.com/Keyri-Co/keyri-checksum-checker-plugin.git")

    plugins {
        create("KeyriChecksumCheckerPlugin") {
            id = "com.keyri.checksumchecker.plugin"
            displayName = "Keyri Checksum Checker Plugin"
            description =
                "Gradle plugin for Android project to get and upload App Bundle files checksums on Keyri. This should increase code tampering protection."
            implementationClass = "com.keyri.checksumchecker.plugin.KeyriChecksumCheckerPlugin"

            tags.set(listOf("Android", "Bundle", "Kotlin", "Security"))
        }
    }
}
