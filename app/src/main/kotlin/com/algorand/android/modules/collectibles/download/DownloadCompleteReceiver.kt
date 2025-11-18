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
