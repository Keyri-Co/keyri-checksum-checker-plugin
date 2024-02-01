package com.keyri.checksumchecker.plugin

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest
import java.util.*

abstract class KeyriChecksumCheckTask : DefaultTask() {

    @Internal
    var appKey: String? = null

    @Internal
    var versionName: String? = null

    @TaskAction
    fun run() {
        val bundlePath = getBundlePath()
        val payload = getBundleChecksumsPayload(bundlePath)

        uploadChecksums(payload)
    }

    private fun getBundlePath(): String {
        val keyriCheckerExt = project.extensions.getByType(KeyriCheckerExtension::class.java)

        appKey = keyriCheckerExt.appKey ?: throw GradleException("You should provide valid appKey")
        versionName = keyriCheckerExt.versionName
            ?: throw GradleException("You should provide valid versionName")

        return keyriCheckerExt.bundleFullPath?.takeIf { File(it).exists() }
            ?: throw GradleException("You should provide valid App Bundle path")
    }

    private fun getBundleChecksumsPayload(bundlePath: String): JsonObject {
        val result = JsonObject()
        val checksums = JsonArray()

        try {
            val bundleFiles = project.zipTree(bundlePath).files

            var fileName = ""
            var fileDigest = ""

            bundleFiles.firstOrNull { it.name == "MANIFEST.MF" }?.readLines()?.asSequence()
                ?.forEach { line ->
                    if (line.contains("Name:")) {
                        fileName = line.removePrefix("Name: ")
                    }

                    if (line.contains("SHA-256-Digest:")) {
                        fileDigest = line.removePrefix("SHA-256-Digest: ")
                    }

                    if (fileName.isNotEmpty() && fileDigest.isNotEmpty()) {
                        val bundleFileEntity = JsonObject()

                        bundleFileEntity.addProperty(fileName, fileDigest)
                        checksums.add(bundleFileEntity)

                        fileName = ""
                        fileDigest = ""
                    }
                } ?: bundleFiles.forEach { bundleFile ->
                val bundleFileEntity = JsonObject()

                bundleFileEntity.addProperty(bundleFile.name, bundleFile.digestAndStringBase64())
                checksums.add(bundleFileEntity)
            }

            project.logger.info("Get ${checksums.size()} Bundle Checksums")

            result.addProperty("osType", "Android")
            result.addProperty("bundlePath", bundlePath)
            result.addProperty("versionName", versionName)

            result.add("checksums", checksums)

            return result
        } catch (e: Exception) {
            throw GradleException(e.message ?: "Failed to read App Bundle files checksums")
        }
    }

    private fun uploadChecksums(payload: JsonObject) {
        try {
            val connection = (URL("https://td.api.keyri.com/register").openConnection() as? HttpURLConnection)?.apply {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                setRequestProperty("appKey", appKey)
                doOutput = true
            }

            connection?.outputStream?.use { outputStream ->
                outputStream.write(payload.toString().toByteArray(Charsets.UTF_8))
            }

            if (connection?.responseCode == 200) {
                val result = connection.inputStream?.readAllBytes()?.decodeToString()

                project.logger.info("App Bundle Checksums uploaded: $result")
            } else {
                throw GradleException("Failed to send App Bundle checksums request with message: " + connection?.responseMessage)
            }
        } catch (e: Exception) {
            if (e is GradleException) {
                throw e
            } else {
                throw GradleException("Failed to send App Bundle checksums request with message: " + e.message)
            }
        }
    }

    private fun File.digestAndStringBase64(): String {
        val buffer = ByteArray(BUFFER_LENGTH)
        val digest = MessageDigest.getInstance("SHA-256")
        var bytesRead: Int
        val inputStream: java.io.InputStream = this.inputStream()

        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
            digest.update(buffer, 0, bytesRead)
        }

        return digest.digest().toStringBase64()
    }

    private fun ByteArray.toStringBase64() = String(Base64.getEncoder().encode(this))

    companion object {
        private const val BUFFER_LENGTH = 1024 * 4
    }
}
