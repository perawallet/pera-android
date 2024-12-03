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

package com.algorand.common.qr.presentation.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.algorand.common.foundation.PeraResult
import com.algorand.common.qr.presentation.view.QrScannerViewEvent.PauseCamera
import com.algorand.common.qr.presentation.view.QrScannerViewEvent.ResumeCamera
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeView
import com.journeyapps.barcodescanner.DefaultDecoderFactory

@Composable
internal actual fun BarcodeScanner(
    modifier: Modifier,
    viewEvent: State<QrScannerViewEvent>,
    onResult: (PeraResult<String>) -> Unit
) {
    val barcodeCallback = remember { getBarcodeCallback(onResult) }
    AndroidView(
        modifier = modifier,
        factory = { context ->
            BarcodeView(context).apply {
                decoderFactory = DefaultDecoderFactory(mutableListOf(BarcodeFormat.QR_CODE))
            }
        },
        update = {
            when (viewEvent.value) {
                PauseCamera -> {
                    it.stopDecoding()
                    it.pause()
                }
                ResumeCamera -> {
                    it.decodeContinuous(barcodeCallback)
                    it.resume()
                }
            }
        }
    )
}

private fun getBarcodeCallback(onResult: (PeraResult<String>) -> Unit): BarcodeCallback {
    return BarcodeCallback { result ->
        if (result.text != null) {
            onResult(PeraResult.Success(result.text))
        } else {
            onResult(PeraResult.Error(IllegalArgumentException()))
        }
    }
}
