buildscript {
    repositories {
        google()
        mavenCentral()
    }

    pluginManager.apply {
        repositories {
            gradlePluginPortal()
        }
    }

    dependencies {
        classpath("com.android.tools.build:gradle:7.3.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.0")
    }
}
