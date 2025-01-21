package com.algorand.android.modules.collectibles.download

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.algorand.android.R

class DownloadCompleteReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE == intent.action) {
            val downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, DEFAULT_DOWNLOAD_ID)
            if (downloadId != DEFAULT_DOWNLOAD_ID) {
                handleDownloadComplete(context, downloadId)
            }
        }
    }

    private fun handleDownloadComplete(context: Context, downloadId: Long) {
        val message = context.getString(R.string.download_completed, downloadId)
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val DEFAULT_DOWNLOAD_ID = -1L
    }
}
