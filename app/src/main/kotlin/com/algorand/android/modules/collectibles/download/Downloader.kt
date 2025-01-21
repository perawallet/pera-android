package com.algorand.android.modules.collectibles.download

import android.net.Uri

interface Downloader {
    fun downloadFile(fileUri: Uri, fileName: String): Long
    fun showDownloadStartMessage(downloadId: Long)
}
