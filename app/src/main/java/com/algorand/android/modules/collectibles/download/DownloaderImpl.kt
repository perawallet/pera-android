package com.algorand.android.modules.collectibles.download

import android.app.DownloadManager
import android.content.Context
import android.content.IntentFilter
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.algorand.android.R
import javax.inject.Inject

class DownloaderImpl @Inject constructor(
    private val context: Context
) : Downloader {
    override fun downloadFile(fileUri: Uri, fileName: String): Long {
        val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val request = DownloadManager.Request(fileUri).apply {
            setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            val title = context.getString(R.string.downloading_title, fileName)
            setTitle(title)
        }
        registerDownloadCompletionReceiver()
        return manager.enqueue(request)
    }

    override fun showDownloadStartMessage(downloadId: Long) {
        val message = context.getString(R.string.download_started, downloadId)
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun registerDownloadCompletionReceiver() {
        ContextCompat.registerReceiver(
            context,
            DownloadCompleteReceiver(),
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }
}
