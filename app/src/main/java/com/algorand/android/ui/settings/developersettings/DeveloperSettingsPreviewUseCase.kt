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

package com.algorand.android.ui.settings.developersettings

import com.algorand.android.accountcore.ui.accountsorting.domain.usecase.GetSortedAccountsByPreference
import com.algorand.android.accountcore.ui.mapper.AccountItemConfigurationMapper
import com.algorand.android.accountcore.ui.usecase.GetAccountDisplayName
import com.algorand.android.core.component.domain.usecase.GetAccountTotalValue
import com.algorand.android.node.domain.usecase.IsSelectedNodeTestnet
import javax.inject.Inject

class DeveloperSettingsPreviewUseCase @Inject constructor(
    private val accountItemConfigurationMapper: AccountItemConfigurationMapper,
    private val getSortedAccountsByPreference: GetSortedAccountsByPreference,
    private val getAccountDisplayName: GetAccountDisplayName,
    private val getAccountTotalValue: GetAccountTotalValue,
    private val isSelectedNodeTestnet: IsSelectedNodeTestnet
) {

    suspend fun getFirstAccountAddress(): String? {
        val sortedAccountListItem = getSortedAccountsByPreference(
            onLoadedAccountConfiguration = {
                val accountValue = getAccountTotalValue(address, includeAlgo = true)
                accountItemConfigurationMapper(
                    accountAddress = address,
                    accountDisplayName = getAccountDisplayName(address),
                    accountType = accountType,
                    accountPrimaryValue = accountValue.primaryAccountValue
                )
            },
            onFailedAccountConfiguration = {
                accountItemConfigurationMapper(
                    accountAddress = this,
                    accountDisplayName = getAccountDisplayName(this),
                    accountType = null
                )
            }
        )
        return sortedAccountListItem.firstOrNull()?.itemConfiguration?.accountAddress
    }

    fun isConnectedToTestnet(): Boolean {
        return isSelectedNodeTestnet()
    }
}
