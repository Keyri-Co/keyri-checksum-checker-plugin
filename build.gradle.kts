plugins {
    checkstyle
}

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

allprojects {
    val kotlinLint by configurations.creating

    dependencies {
        kotlinLint("com.pinterest:ktlint:0.48.2") {
            attributes {
                attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.EXTERNAL))
            }
        }
    }

    repositories {
        google()
        mavenCentral()
    }

    tasks.register("checkJavaStyle", Checkstyle::class.java) {
        isShowViolations = true
        configFile = file("../settings/checkstyle.xml")
        setSource("src/main/java")
        include("**/*.java")
        exclude("**/gen/**")
        exclude("**/R.java")
        exclude("**/BuildConfig.java")

        classpath = files()
    }

    tasks.register<JavaExec>("ktlint") {
        description = "Check Kotlin code style."
        mainClass.set("com.pinterest.ktlint.Main")
        classpath = kotlinLint
        args("src/**/*.kt")
    }

    tasks.register<JavaExec>("ktlintFormat") {
        group = "formatting"
        description = "Fix Kotlin code style deviations."
        mainClass.set("com.pinterest.ktlint.Main")
        classpath = kotlinLint
        args("-F", "src/**/*.kt")
    }
}

tasks.create<Delete>("clean") {
    delete(rootProject.buildDir)
}
