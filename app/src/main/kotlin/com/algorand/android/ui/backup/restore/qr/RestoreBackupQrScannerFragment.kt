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

package com.algorand.android.ui.backup.restore.qr

import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.fragment.app.viewModels
import com.algorand.android.R
import com.algorand.android.core.BaseFragment
import com.algorand.android.databinding.FragmentQrCodeScannerBinding
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.models.StatusBarConfiguration
import com.algorand.android.utils.CAMERA_PERMISSION
import com.algorand.android.utils.CAMERA_PERMISSION_REQUEST_CODE
import com.algorand.android.utils.isPermissionGranted
import com.algorand.android.utils.requestPermissionFromUser
import com.algorand.android.utils.viewbinding.viewBinding
import com.google.zxing.BarcodeFormat
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RestoreBackupQrScannerFragment : BaseFragment(R.layout.fragment_qr_code_scanner) {

    private val viewModel: RestoreBackupQrScannerViewModel by viewModels()

    private val binding by viewBinding(FragmentQrCodeScannerBinding::bind)

    private val statusBarConfiguration = StatusBarConfiguration(
        backgroundColor = R.color.transparent,
        showNodeStatus = false
    )

    override val fragmentConfiguration: FragmentConfiguration =
        FragmentConfiguration(statusBarConfiguration = statusBarConfiguration)

    private var hasHandledPayload: Boolean = false

    private val onWindowFocusChangeListener = ViewTreeObserver.OnWindowFocusChangeListener {
        resumeCameraIfPossibleOrPause()
    }

    private val barcodeCallback: BarcodeCallback = object : BarcodeCallback {
        override fun barcodeResult(barcodeResult: BarcodeResult?) {
            if (hasHandledPayload || barcodeResult == null) return
            binding.cameraPreview.pause()
            handleScannedQr(barcodeResult.text)
        }

        override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) = Unit
    }

    private val isCameraPermissionGranted: Boolean
        get() = view?.context?.isPermissionGranted(CAMERA_PERMISSION) == true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.titleTextView.text = getString(R.string.scan_backup_sync_qr_title)
        binding.leftArrowButton.setOnClickListener { navBack() }
        if (isCameraPermissionGranted) {
            setupBarcodeView()
        } else {
            requestPermissionFromUser(CAMERA_PERMISSION, CAMERA_PERMISSION_REQUEST_CODE, shouldShowAlways = true)
        }
    }

    override fun onResume() {
        super.onResume()
        resumeCameraIfPossibleOrPause()
        view?.viewTreeObserver?.addOnWindowFocusChangeListener(onWindowFocusChangeListener)
    }

    override fun onPause() {
        super.onPause()
        binding.cameraPreview.pause()
        view?.viewTreeObserver?.removeOnWindowFocusChangeListener(onWindowFocusChangeListener)
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            setupBarcodeView()
            resumeCameraIfPossibleOrPause()
        }
    }

    private fun handleScannedQr(rawValue: String) {
        if (!viewModel.isSyncQrPayload(rawValue)) {
            showGlobalError(getString(R.string.scanned_qr_is_not_valid))
            resumeCameraIfPossibleOrPause()
            return
        }
        hasHandledPayload = true
        nav(
            RestoreBackupQrScannerFragmentDirections
                .actionRestoreBackupQrScannerFragmentToRestoreBackupPinFragment(encryptedPayload = rawValue)
        )
    }

    private fun setupBarcodeView() {
        with(binding.cameraPreview) {
            cameraSettings.isContinuousFocusEnabled = true
            decoderFactory = DefaultDecoderFactory(mutableListOf(BarcodeFormat.QR_CODE))
            decodeContinuous(barcodeCallback)
        }
    }

    private fun resumeCameraIfPossibleOrPause() {
        with(binding.cameraPreview) {
            view?.let {
                if (isCameraPermissionGranted && it.hasWindowFocus()) {
                    pause()
                    resume()
                    return
                }
            }
            pause()
        }
    }
}
