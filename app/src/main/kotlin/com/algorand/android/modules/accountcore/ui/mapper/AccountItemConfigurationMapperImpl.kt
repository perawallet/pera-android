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

package com.algorand.android.modules.accountcore.ui.mapper

import com.algorand.android.models.ButtonConfiguration
import com.algorand.android.modules.accountcore.ui.model.AccountDisplayName
import com.algorand.android.modules.accountcore.ui.model.BaseItemConfiguration
import com.algorand.android.modules.accounticon.ui.model.AccountIconDrawablePreview
import com.algorand.wallet.account.detail.domain.model.AccountType
import java.math.BigDecimal
import javax.inject.Inject

internal class AccountItemConfigurationMapperImpl @Inject constructor() : AccountItemConfigurationMapper {

    override fun invoke(
        accountAddress: String,
        accountDisplayName: AccountDisplayName,
        accountIconDrawablePreview: AccountIconDrawablePreview?,
        accountType: AccountType?,
        accountPrimaryValueText: String?,
        accountSecondaryValueText: String?,
        accountPrimaryValue: BigDecimal?,
        accountSecondaryValue: BigDecimal?,
        accountAssetCount: Int?,
        showWarningIcon: Boolean?,
        dragButtonConfiguration: ButtonConfiguration?,
        startSmallIconResource: Int?
    ): BaseItemConfiguration.AccountItemConfiguration {
        return BaseItemConfiguration.AccountItemConfiguration(
            accountAddress = accountAddress,
            accountIconDrawablePreview = accountIconDrawablePreview,
            accountDisplayName = accountDisplayName,
            primaryValueText = accountPrimaryValueText,
            secondaryValueText = accountSecondaryValueText,
            primaryValue = accountPrimaryValue,
            secondaryValue = accountSecondaryValue,
            showWarning = showWarningIcon,
            dragButtonConfiguration = dragButtonConfiguration,
            accountType = accountType,
            accountAssetCount = accountAssetCount,
            startSmallIconResource = startSmallIconResource
        )
    }
}
