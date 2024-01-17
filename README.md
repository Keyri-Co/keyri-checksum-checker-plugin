<p style="text-align:center">
    <a href="">
        <img src="https://keyri.com/wp-content/uploads/2022/09/Keyri-Grey-Logo-Website-300x147.png" width=200  alt=""/>
    </a>
</p>

<p style="text-align:center">Gradle plugin for creating and check checksums with Keyri SDK on Android</p>

# Overview

This repository contains the open source code for Keyri Checksum Checker Plugin.

[![Lint](https://github.com/Keyri-Co/keyri-checksum-checker-plugin/actions/workflows/lint.yml/badge.svg)](https://github.com/Keyri-Co/keyri-checksum-checker-plugin/actions/workflows/lint.yml)
[![GitHub release](https://img.shields.io/github/release/Keyri-Co/keyri-checksum-checker-plugin.svg?maxAge=10)](https://github.com/Keyri-Co/keyri-checksum-checker-plugin/releases)

## Contents

* [Integration](#integration)
* [Usage](#usage)

The latest source code of the Keyri Android SDK can be found
here: [Releases](https://github.com/Keyri-Co/keyri-checksum-checker-plugin/releases)

### **Integration**

Using the plugins DSL:

```kotlin
plugins {
    id("com.keyri.checksumchecker.plugin") version "$latestPluginVersion"
}
```

Using legacy plugin application:

```kotlin
buildscript {
    repositories {
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        }
    }
    dependencies {
        classpath("com.keyri:plugin:$latestPluginVersion")
    }
}

apply(plugin = "com.keyri.checksumchecker.plugin")
```

### **Usage**

[//]: # (TODO: Add implementation)
