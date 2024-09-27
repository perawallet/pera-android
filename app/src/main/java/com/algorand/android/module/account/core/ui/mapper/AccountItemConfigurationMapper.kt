@file:Suppress("LongParameterList")
package com.algorand.android.module.account.core.ui.mapper

import com.algorand.android.module.account.core.component.detail.domain.model.AccountType
import com.algorand.android.module.account.core.ui.model.AccountDisplayName
import com.algorand.android.module.account.core.ui.model.AccountIconDrawablePreview
import com.algorand.android.module.account.core.ui.model.BaseItemConfiguration
import com.algorand.android.module.account.core.ui.model.ButtonConfiguration
import java.math.BigDecimal

interface AccountItemConfigurationMapper {

    operator fun invoke(
        accountAddress: String,
        accountDisplayName: AccountDisplayName,
        accountIconDrawablePreview: AccountIconDrawablePreview? = null,
        accountType: AccountType? = null,
        accountPrimaryValueText: String? = null,
        accountSecondaryValueText: String? = null,
        accountPrimaryValue: BigDecimal? = null,
        accountSecondaryValue: BigDecimal? = null,
        accountAssetCount: Int? = null,
        showWarningIcon: Boolean? = null,
        dragButtonConfiguration: ButtonConfiguration? = null,
        startSmallIconResource: Int? = null
    ): BaseItemConfiguration.AccountItemConfiguration
}
