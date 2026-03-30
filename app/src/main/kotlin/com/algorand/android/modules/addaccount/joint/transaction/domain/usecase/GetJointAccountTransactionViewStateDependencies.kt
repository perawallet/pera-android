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

package com.algorand.android.modules.addaccount.joint.transaction.domain.usecase

import com.algorand.android.modules.accountcore.ui.usecase.GetAccountDisplayName
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountIconDrawablePreview
import com.algorand.android.modules.verificationtier.ui.decider.VerificationTierConfigurationDecider
import com.algorand.android.ui.device.usecase.GetDeviceConfig
import com.algorand.wallet.account.info.domain.usecase.GetAccountRekeyAdminAddress
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccounts
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccountsAddresses
import com.algorand.wallet.algosdk.transaction.usecase.ParseTransactionMessagePack
import com.algorand.wallet.asset.domain.usecase.FetchAsset
import com.algorand.wallet.jointaccount.transaction.domain.usecase.GetSignRequestWithSignatures
import com.algorand.wallet.utils.date.TimeProvider
import javax.inject.Inject

internal data class GetJointAccountTransactionViewStateDependencies @Inject constructor(
    val getSignRequestWithSignatures: GetSignRequestWithSignatures,
    val parseTransactionMessagePack: ParseTransactionMessagePack,
    val getAccountDisplayName: GetAccountDisplayName,
    val getAccountIconDrawablePreview: GetAccountIconDrawablePreview,
    val getDeviceConfig: GetDeviceConfig,
    val getLocalAccountsAddresses: GetLocalAccountsAddresses,
    val getLocalAccounts: GetLocalAccounts,
    val getJointAccountSignerItems: GetJointAccountSignerItems,
    val formatAlgoAsDisplayCurrency: FormatAlgoAsDisplayCurrency,
    val timeProvider: TimeProvider,
    val getAccountRekeyAdminAddress: GetAccountRekeyAdminAddress,
    val fetchAsset: FetchAsset,
    val verificationTierConfigurationDecider: VerificationTierConfigurationDecider
)
