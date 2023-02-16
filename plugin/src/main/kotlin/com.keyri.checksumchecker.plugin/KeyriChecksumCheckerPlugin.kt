package com.keyri.checksumchecker.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class KeyriChecksumCheckerPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.extensions.create("keyriChecker", KeyriCheckerExtension::class.java)
        target.tasks.register("keyriChecksumCheck", KeyriChecksumCheckTask::class.java)
    }
}
