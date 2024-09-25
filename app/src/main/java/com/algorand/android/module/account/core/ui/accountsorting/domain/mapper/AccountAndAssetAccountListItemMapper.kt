package com.algorand.android.module.account.core.ui.accountsorting.domain.mapper

import com.algorand.android.module.account.core.ui.accountsorting.domain.model.AccountAndAssetListItem
import com.algorand.android.module.account.core.ui.model.BaseItemConfiguration.AccountItemConfiguration

internal interface AccountAndAssetAccountListItemMapper {
    operator fun invoke(itemConfiguration: AccountItemConfiguration): AccountAndAssetListItem.AccountListItem
}
