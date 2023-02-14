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

    @Internal
    var apkID: String? = null

    @TaskAction
    fun run() {
        val apkPath = getApkPath()
        val payload = getAPKChecksumsPayload(apkPath)

        uploadChecksums(payload)
    }

    private fun getApkPath(): String {
        val keyriCheckerExt = project.extensions.getByType(KeyriCheckerExtension::class.java)

        appKey = keyriCheckerExt.appKey ?: throw GradleException("You should provide valid App Key")
        apkID = keyriCheckerExt.apkID ?: throw GradleException("You should provide valid APK ID")

        return keyriCheckerExt.apkFullPath?.takeIf { it.isNotEmpty() }
            ?: throw GradleException("You should provide valid APK path")
    }

    private fun getAPKChecksumsPayload(apkPath: String): JsonObject {
        val result = JsonObject()
        val checksums = JsonArray()

        // TODO Use META-INF/MANIFEST.MF to get checksums

        try {
            val apkFiles = project.zipTree(apkPath).files

            apkFiles.firstOrNull { it.name == "MANIFEST.MF" }?.readLines()?.forEach { // TODO as sequence?
                // TODO Fetch checksums here
            } ?: apkFiles.forEach { apkFile ->
                val apkFileEntity = JsonObject()

                apkFileEntity.addProperty(apkFile.name, apkFile.digestAndString())
                checksums.add(apkFileEntity)
            }

            println("Get ${checksums.size()} APK Checksums")

            result.addProperty("osType", "Android")
            result.addProperty("apkPath", apkPath)
            result.addProperty("apkID", apkID)

            result.add("checksums", checksums)

            return result
        } catch (e: Exception) {
            throw GradleException(e.message ?: "Failed to read APK files checksums")
        }
    }

    private fun uploadChecksums(payload: JsonObject) {
        try {
            // TODO Add url
            val connection = (URL("").openConnection() as? HttpURLConnection)?.apply {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                doOutput = true
            }

            connection?.outputStream?.write(payload.toString().encodeToByteArray())

            if (connection?.responseCode == 200) {
                val result = connection.inputStream?.readAllBytes()?.decodeToString()

                println("APK Checksums uploaded: $result")
            } else {
                throw GradleException("Failed to send APK checksums request with message: " + connection?.responseMessage)
            }
        } catch (e: Exception) {
            if (e is GradleException) {
                throw e
            } else {
                throw GradleException("Failed to send APK checksums request with message: " + e.message)
            }
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
