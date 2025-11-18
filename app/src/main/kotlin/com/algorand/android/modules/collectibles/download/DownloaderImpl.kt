/*
 * Copyright 2022-2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

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
