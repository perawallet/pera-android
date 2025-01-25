/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.modules.qrscanning

import android.content.Context
import android.util.AttributeSet
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.AbstractComposeView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.algorand.common.qr.presentation.view.QrScanner
import com.algorand.common.qr.presentation.view.QrScannerViewEvent
import kotlinx.coroutines.flow.MutableStateFlow

class QrScannerView(context: Context, attrs: AttributeSet? = null) : AbstractComposeView(context, attrs) {

    private val qrScannerViewEventFlow = MutableStateFlow<QrScannerViewEvent>(QrScannerViewEvent.ResumeCamera)

    private var onQrScanned: (String) -> Unit = {}

    fun init(onQrScanned: (String) -> Unit) {
        this.onQrScanned = onQrScanned
    }

    fun pause() {
        qrScannerViewEventFlow.value = QrScannerViewEvent.PauseCamera
    }

    fun resume() {
        qrScannerViewEventFlow.value = QrScannerViewEvent.ResumeCamera
    }

    @Composable
    override fun Content() {
        QrScanner(
            viewEvent = qrScannerViewEventFlow.collectAsStateWithLifecycle(),
            onQrScanned = onQrScanned
        )
    }
}
