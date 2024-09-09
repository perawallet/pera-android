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

package com.algorand.android.accountcore.ui.accountselection.usecase

import com.algorand.android.accountcore.ui.mapper.AccountItemConfigurationMapper
import com.algorand.android.accountcore.ui.model.BaseItemConfiguration
import com.algorand.android.accountcore.ui.usecase.GetAccountDisplayName
import com.algorand.android.accountcore.ui.usecase.GetAccountIconDrawablePreview
import com.algorand.android.core.component.detail.domain.model.AccountDetail
import com.algorand.android.core.component.domain.usecase.GetAccountTotalValue
import com.algorand.android.formatting.formatAsCurrency
import javax.inject.Inject

internal class CreateLoadedAccountConfigurationUseCase @Inject constructor(
    private val getAccountTotalValue: GetAccountTotalValue,
    private val accountItemConfigurationMapper: AccountItemConfigurationMapper,
    private val getAccountDisplayName: GetAccountDisplayName,
    private val getAccountIconDrawablePreview: GetAccountIconDrawablePreview
) : CreateLoadedAccountConfiguration {

    override suspend fun invoke(
        accountDetail: AccountDetail,
        showHoldings: Boolean,
        selectedCurrencySymbol: String
    ): BaseItemConfiguration.AccountItemConfiguration {
        val primaryAccountValue = getAccountTotalValue(accountDetail.address, includeAlgo = true).primaryAccountValue
        val accountPrimaryValueText = if (showHoldings) {
            primaryAccountValue.formatAsCurrency(
                symbol = selectedCurrencySymbol,
                isCompact = true,
                isFiat = true
            )
        } else {
            null
        }
        return accountItemConfigurationMapper(
            accountAddress = accountDetail.address,
            accountDisplayName = getAccountDisplayName(accountDetail.address),
            accountIconDrawablePreview = getAccountIconDrawablePreview(accountDetail.address),
            accountPrimaryValueText = accountPrimaryValueText,
            accountPrimaryValue = primaryAccountValue,
            accountType = accountDetail.accountType
        )
    }
}
