package com.algorand.android.accountcore.ui.accountsorting.domain.mapper

import com.algorand.android.accountcore.ui.accountsorting.domain.model.AccountAndAssetListItem
import com.algorand.android.accountcore.ui.model.BaseItemConfiguration.AccountItemConfiguration

internal interface AccountAndAssetAccountListItemMapper {
    operator fun invoke(itemConfiguration: AccountItemConfiguration): AccountAndAssetListItem.AccountListItem
}
