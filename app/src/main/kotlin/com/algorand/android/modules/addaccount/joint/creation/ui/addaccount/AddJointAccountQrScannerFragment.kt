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

package com.algorand.android.modules.addaccount.joint.creation.ui.addaccount

import com.algorand.android.R
import com.algorand.android.modules.qrscanning.BaseQrScannerFragment
import com.algorand.android.utils.setNavigationResult
import com.algorand.wallet.deeplink.model.DeepLink

class AddJointAccountQrScannerFragment : BaseQrScannerFragment(R.id.addJointAccountQrScannerFragment) {

    override val titleTextResId: Int
        get() = R.string.scan_an_algorand

    override fun onDeepLinkNotHandled(deepLink: DeepLink) {
        showGlobalError(getString(R.string.the_scanned_qr_is_not))
    }

    override fun onAccountAddressDeeplink(address: String, label: String?): Boolean {
        return navBackWithResult(address)
    }

    private fun navBackWithResult(address: String): Boolean {
        setNavigationResult(ACCOUNT_ADDRESS_QR_SCAN_RESULT_KEY, address)
        return true.also { navBack() }
    }

    companion object {
        const val ACCOUNT_ADDRESS_QR_SCAN_RESULT_KEY = "add_joint_account_qr_scan_result"
    }
}
