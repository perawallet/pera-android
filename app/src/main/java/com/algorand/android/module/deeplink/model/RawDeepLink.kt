/*
 *   ~ Copyright 2022 Pera Wallet, LDA
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License
 *   -->
 */

package com.algorand.android.module.deeplink.model

import java.math.BigInteger

internal data class RawDeepLink(
    val rawUrl: String,
    val accountAddress: String? = null,
    val walletConnectUrl: String? = null,
    val assetId: Long? = null,
    val amount: BigInteger? = null,
    val note: String? = null,
    val xnote: String? = null,
    val mnemonic: String? = null,
    val label: String? = null,
    val transactionStatus: String? = null,
    val transactionId: String? = null,
    val webImportQrCode: com.algorand.android.module.deeplink.model.WebImportQrCode? = null,
    val notificationGroupType: com.algorand.android.module.deeplink.model.NotificationGroupType? = null,
    val url: String? = null
)
