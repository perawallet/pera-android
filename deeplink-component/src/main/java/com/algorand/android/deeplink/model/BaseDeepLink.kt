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

package com.algorand.android.deeplink.model

import java.math.BigInteger

sealed interface BaseDeepLink {

    /**
     * Examples;
     *  - perawallet://WOLFYW4VEVVGEGVLQWEL4EMJ5SFCD3UCNKDH2DCUB5HQ6HLM6URZBMPXLI
     *  - algorand://WOLFYW4VEVVGEGVLQWEL4EMJ5SFCD3UCNKDH2DCUB5HQ6HLM6URZBMPXLI
     */
    data class AccountAddressDeepLink internal constructor(
        val accountAddress: String,
        val label: String?
    ) : BaseDeepLink

    /**
     * Examples;
     *  - algorand://?amount=0&asset=776191503
     *  - perawallet://?amount=0&asset=77619150
     */
    data class AssetOptInDeepLink internal constructor(val assetId: Long) : BaseDeepLink

    /**
     * ALGO transfer (public key, empty asset id, amount, note, xnote)
     * Examples;
     *  - algorand://WOLFYW4VEVVGEGVLQWEL4EMJ5SFCD3UCNKDH2DCUB5HQ6HLM6URZBMPXLI?amount=1000000&note=1_ALGO_Transfer
     *  - perawallet://WOLFYW4VEVVGEGVLQWEL4EMJ5SFCD3UCNKDH2DCUB5HQ6HLM6URZBMPXLI?amount=1000000&note=1_ALGO_Transfer
     *
     * ASA transfer (public key, asset ID, amount, note, xnote)
     *  - algorand://WOLFYW4VEVVGEGVLQWEL4EMJ5SFCD3UCNKDH2DCUB5HQ6HLM6URZBMPXLI?amount=1&asset=226701642&xnote=Uneditable_1_USDC_Transfer_Note
     *  - perawallet://WOLFYW4VEVVGEGVLQWEL4EMJ5SFCD3UCNKDH2DCUB5HQ6HLM6URZBMPXLI?amount=1&asset=226701642&xnote=Uneditable_1_USDC_Transfer_Note
     */
    data class AssetTransferDeepLink internal constructor(
        val assetId: Long,
        val receiverAccountAddress: String,
        val amount: BigInteger,
        val note: String?,
        val xnote: String?,
        val label: String?
    ) : BaseDeepLink

    /**
     * Examples;
     * wc://wc:b562a118-0cbd-4f4f-92af-e58bf0a9dfb8@1?bridge=https%3A%2F%2Fwallet-connect-d.perawallet.app&key=672a4fbd212bfdbf6e0c8a858d9ab1577df169e7eac74c7175b9a3fd0faea889
     * perawallet-wc://wc:b562a118-0cbd-4f4f-92af-e58bf0a9dfb8@1?bridge=https%3A%2F%2Fwallet-connect-d.perawallet.app&key=672a4fbd212bfdbf6e0c8a858d9ab1577df169e7eac74c7175b9a3fd0faea889
     */
    data class WalletConnectConnectionDeepLink internal constructor(val url: String) : BaseDeepLink

    data class MnemonicDeepLink internal constructor(val mnemonic: String) : BaseDeepLink

    data class WebImportQrCodeDeepLink internal constructor(val webImportQrCode: WebImportQrCode) : BaseDeepLink

    data class NotificationDeepLink internal constructor(
        val address: String,
        val assetId: Long,
        val notificationGroupType: NotificationGroupType,
        val isThereAnyAccountWithPublicKey: Boolean
    ) : BaseDeepLink

    data class UndefinedDeepLink internal constructor(val url: String) : BaseDeepLink
}
