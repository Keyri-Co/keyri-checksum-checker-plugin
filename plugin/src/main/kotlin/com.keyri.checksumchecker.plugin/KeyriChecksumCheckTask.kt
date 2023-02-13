package com.keyri.checksumchecker.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.impldep.com.google.gson.JsonArray
import org.gradle.internal.impldep.com.google.gson.JsonObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest

abstract class KeyriChecksumCheckTask : DefaultTask() {

    @Internal
    var appKey: String? = null

    @TaskAction
    fun run() {
        val artifactPath = getArtifactPath()
        val payload = getAPKChecksumsPayload(artifactPath)

        uploadChecksums(payload)
    }

    // TODO Test all
    // TODO Add documentation and readme
    // TODO Publish

    private fun getArtifactPath(): String {
        val keyriCheckerExt = project.extensions.getByType(KeyriCheckerExtension::class.java)

        appKey = keyriCheckerExt.appKey ?: throw GradleException("You should provide valid App Key")

        return keyriCheckerExt.apkRelativePath?.takeIf { it.isNotEmpty() }
            ?: throw GradleException("You should provide valid APK path")
    }

    private fun getAPKChecksumsPayload(artifactPath: String): JsonObject {
        val result = JsonObject()
        val checksums = JsonArray()

        // TODO Add payload id (version code, name, something else)
        // TODO Add try-catch blocks?

        project.file(artifactPath).let {
            project.zipTree(it.path)
                .files
                .forEach { apkFile ->
                    val apkFileEntity = JsonObject()

                    apkFileEntity.addProperty(apkFile.name, apkFile.digestAndString())
                    checksums.add(apkFileEntity)
                }
        }

        // TODO Need to find the way to identify artifact

        // TODO And check META-INF/CERT.SF checksums

        println("Get ${checksums.size()} APK Checksums")

        result.addProperty("osType", "Android")
        result.addProperty("artifactPath", artifactPath)
        result.addProperty("appVersionCode", appVersionCode)
        result.addProperty("appVersionName", appVersionName)
        result.addProperty("sdkVersion", sdkVersion)
        result.addProperty("appSignature", appSignature)

        result.add("checksums", checksums)

        return result
    }

    private fun uploadChecksums(payload: JsonObject) {
        val url = "" // TODO Add url

        val connection = (URL(url).openConnection() as? HttpURLConnection)?.apply {
            requestMethod = "POST"
            setRequestProperty("Content-Type", "application/json; charset=UTF-8")
            doOutput = true
        }

        connection?.outputStream?.write(payload.toString().encodeToByteArray())

        if (connection?.responseCode == 200) {
            val result = connection.inputStream?.readAllBytes()?.decodeToString()

            println("APK Checksums uploaded: $result")
        } else {
            throw GradleException("Failed to send request with message: " + connection?.responseMessage)
        }
    }

    private fun File.digestAndString(): String {
        val buffer = ByteArray(BUFFER_LENGTH)
        val digest = MessageDigest.getInstance("SHA-1")
        var bytesRead: Int
        val inputStream: java.io.InputStream = this.inputStream()

        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
            digest.update(buffer, 0, bytesRead)
        }

        return digest.digest().encodeHex()
    }

    private fun ByteArray.encodeHex(): String =
        joinToString(separator = "") { eachByte -> "%02x".format(eachByte) }

    companion object {
        private const val BUFFER_LENGTH = 1024 * 4
    }
}
