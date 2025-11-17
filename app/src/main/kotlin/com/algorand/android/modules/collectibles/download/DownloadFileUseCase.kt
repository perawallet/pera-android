package com.algorand.android.modules.collectibles.download

import androidx.core.net.toUri
import javax.inject.Inject

class DownloadFileUseCase @Inject constructor(
    private val downloader: Downloader
) {

    fun execute(fileUrl: String, fileName: String?): Long {
        val fileUri = fileUrl.toUri()
        val validFileName = fileName ?: fileUri.lastPathSegment.orEmpty()
        val downloadId = downloader.downloadFile(fileUri, validFileName)

        downloader.showDownloadStartMessage(downloadId)
        return downloadId
    }
}
