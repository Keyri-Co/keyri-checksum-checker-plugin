package com.keyri.checksumchecker.plugin

abstract class KeyriCheckerExtension {
    var apkFullPath: String? = null
    var apkID: String? = null
    internal var appKey: String? = null

    fun setAppKey(appKey: String) {
        this.appKey = appKey
    }
}
