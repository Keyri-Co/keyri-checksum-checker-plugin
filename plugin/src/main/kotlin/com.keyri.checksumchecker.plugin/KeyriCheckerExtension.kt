package com.keyri.checksumchecker.plugin

class KeyriCheckerExtension {
    var apkRelativePath: String? = null
    internal var appKey: String? = null

    // TODO Add more options: tag, version, custom?
    fun setAppKey(appKey: String) {
        this.appKey = appKey
    }
}
