package com.algorand.android.module.account.core.ui.mapper

import com.algorand.android.module.account.core.ui.model.*
import com.algorand.android.module.account.core.component.detail.domain.model.AccountType
import com.algorand.android.module.account.core.ui.model.AccountDisplayName
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