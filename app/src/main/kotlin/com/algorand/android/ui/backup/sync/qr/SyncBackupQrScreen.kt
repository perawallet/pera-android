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

package com.algorand.android.ui.backup.sync.qr

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.algorand.android.R
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.button.PeraSecondaryButton
import com.algorand.android.utils.getQrCodeBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val QrSizeDp = 280.dp

@Composable
fun SyncBackupQrScreen(
    encryptedPayload: String,
    onCloseClick: () -> Unit
) {
    val density = LocalDensity.current
    val qrSizePx = remember(density) { with(density) { QrSizeDp.toPx().toInt() } }
    val qrBitmap by produceState<Bitmap?>(initialValue = null, encryptedPayload, qrSizePx) {
        value = withContext(Dispatchers.Default) {
            getQrCodeBitmap(qrSizePx, encryptedPayload)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.sync_backup_qr_description),
            style = PeraTheme.typography.body.regular.sans,
            color = PeraTheme.colors.text.main
        )
        Spacer(modifier = Modifier.weight(0.4f))
        Box(
            modifier = Modifier.size(QrSizeDp),
            contentAlignment = Alignment.Center
        ) {
            qrBitmap?.let { bitmap ->
                Image(
                    modifier = Modifier.size(QrSizeDp),
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = null
                )
            }
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(color = Color.Black, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(28.dp),
                    painter = painterResource(R.drawable.ic_pera),
                    tint = Color.Unspecified,
                    contentDescription = null
                )
            }
        }
        Spacer(modifier = Modifier.weight(0.6f))
        PeraSecondaryButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            onClick = onCloseClick,
            text = stringResource(R.string.close)
        )
    }
}
