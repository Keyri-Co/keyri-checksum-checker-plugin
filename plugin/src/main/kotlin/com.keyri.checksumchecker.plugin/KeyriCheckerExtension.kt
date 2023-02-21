package com.keyri.checksumchecker.plugin

open class KeyriCheckerExtension {
    var apkFullPath: String? = null
    var apkID: String? = null
    internal var appKey: String? = null

    fun setAppKey(appKey: String) {
        this.appKey = appKey
    }
}
