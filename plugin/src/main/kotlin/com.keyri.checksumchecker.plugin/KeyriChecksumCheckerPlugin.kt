package com.keyri.checksumchecker.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class KeyriChecksumCheckerPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.extensions.create("keyriChecker", KeyriCheckerExtension::class.java)
        project.tasks.register("keyriChecksumCheck", KeyriChecksumCheckTask::class.java)
    }
}
