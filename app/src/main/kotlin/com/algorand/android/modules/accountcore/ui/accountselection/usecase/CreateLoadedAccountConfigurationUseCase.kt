/*
 * Copyright 2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.modules.accountcore.ui.accountselection.usecase

import com.algorand.android.modules.accountcore.ui.mapper.AccountItemConfigurationMapper
import com.algorand.android.modules.accountcore.ui.model.BaseItemConfiguration
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountDisplayName
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountIconDrawablePreview
import com.algorand.android.modules.accounts.lite.domain.model.AccountLite
import com.algorand.android.utils.formatAsCurrency
import javax.inject.Inject

internal class CreateLoadedAccountConfigurationUseCase @Inject constructor(
    private val accountItemConfigurationMapper: AccountItemConfigurationMapper,
    private val getAccountDisplayName: GetAccountDisplayName,
    private val getAccountIconDrawablePreview: GetAccountIconDrawablePreview
) : CreateLoadedAccountConfiguration {

    override suspend fun invoke(
        accountLite: AccountLite,
        showHoldings: Boolean,
        selectedCurrencySymbol: String
    ): BaseItemConfiguration.AccountItemConfiguration {
        val accountPrimaryValueText = if (showHoldings) {
            accountLite.cachedInfo?.primaryAccountValue?.formatAsCurrency(
                symbol = selectedCurrencySymbol,
                isCompact = true,
                isFiat = true
            )
        } else {
            null
        }

        return accountItemConfigurationMapper(
            accountAddress = accountLite.address,
            accountDisplayName = getAccountDisplayName(accountLite),
            accountIconDrawablePreview = getAccountIconDrawablePreview(accountLite),
            accountPrimaryValueText = accountPrimaryValueText,
            accountPrimaryValue = accountLite.cachedInfo?.primaryAccountValue,
            accountType = accountLite.cachedInfo?.type
        )
    }
}
