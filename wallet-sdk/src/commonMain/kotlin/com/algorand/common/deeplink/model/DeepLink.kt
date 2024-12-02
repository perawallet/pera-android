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

package com.algorand.common.deeplink.model

/**
 * Model classes for supported DeepLinks & AppLinks
 * Can be tested by executing the command below;
 * adb shell am start -a android.intent.action.VIEW -d "DEEPLINK" com.algorand.android
 */
sealed interface DeepLink {

    /**
     * Examples;
     *  - perawallet://WOLFYW4VEVVGEGVLQWEL4EMJ5SFCD3UCNKDH2DCUB5HQ6HLM6URZBMPXLI
     *  - algorand://WOLFYW4VEVVGEGVLQWEL4EMJ5SFCD3UCNKDH2DCUB5HQ6HLM6URZBMPXLI
     */
    data class AccountAddress(val address: String, val label: String?) : DeepLink

    /**
     * Examples;
     *  - algorand://?amount=0&asset=776191503
     *  - perawallet://?amount=0&asset=77619150
     */
    data class AssetOptIn(val assetId: Long) : DeepLink

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
    data class AssetTransfer(
        val assetId: Long,
        val receiverAccountAddress: String,
        val amount: String,
        val note: String?,
        val xnote: String?,
        val label: String?
    ) : DeepLink

    /**
     * Examples;
     * wc://wc:b562a118-0cbd-4f4f-92af-e58bf0a9dfb8@1?bridge=https%3A%2F%2Fwallet-connect-d.perawallet.app&key=672a4fbd212bfdbf6e0c8a858d9ab1577df169e7eac74c7175b9a3fd0faea889
     * perawallet-wc://wc:b562a118-0cbd-4f4f-92af-e58bf0a9dfb8@1?bridge=https%3A%2F%2Fwallet-connect-d.perawallet.app&key=672a4fbd212bfdbf6e0c8a858d9ab1577df169e7eac74c7175b9a3fd0faea889
     */
    data class WalletConnectConnection(val uri: String) : DeepLink

    data class Mnemonic(val mnemonic: String) : DeepLink

    data class WebImportQrCode(val backupId: String, val encryptionKey: String) : DeepLink

    data class DiscoverBrowser(val webUrl: String) : DeepLink

    data class Notification(
        val address: String,
        val assetId: Long,
        val notificationGroupType: NotificationGroupType
    ) : DeepLink

    data class AssetInbox(
        val address: String,
        val notificationGroupType: NotificationGroupType
    ) : DeepLink

    /**
     * ALGO transfer (public key, empty asset id, amount, note, xnote)
     * Examples
     * algorand://7IBEAXHK62XEJATU6Q4QYQCDFY475CEKNXGLYQO6QSGCLVMMK4SLVTYLMY?
     * type=keyreg
     * &selkey=-lfw-Y04lTnllJfncgMjXuAePe8i8YyVeoR9c1Xi78c
     * &sprfkey=3NoXc2sEWlvQZ7XIrwVJjgjM30ndhvwGgcqwKugk1u5W_iy_JITXrykuy0hUvAxbVv0njOgBPtGFsFif3yLJpg
     * &votefst=1300
     * &votekd=100
     * &votekey=UU8zLMrFVfZPnzbnL6ThAArXFsznV3TvFVAun2ONcEI
     * &votelst=11300
     * &fee=2000000
     * &note=Consensus%2Bparticipation%2Bftw
     */
    data class KeyReg(
        val senderAddress: String,
        val type: String,
        val voteKey: String?,
        val selkey: String?,
        val sprfkey: String?,
        val votefst: String?,
        val votelst: String?,
        val votekd: String?,
        val fee: String?,
        val note: String?,
        val xnote: String?
    ) : DeepLink

    data class Undefined(val url: String) : DeepLink
}
