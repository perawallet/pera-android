package com.algorand.android.accountcore.ui.accountsorting.domain.mapper

import com.algorand.android.accountcore.ui.accountsorting.domain.model.AccountAndAssetListItem.AccountListItem
import com.algorand.android.accountcore.ui.model.BaseItemConfiguration
import javax.inject.Inject

internal class AccountAndAssetAccountListItemMapperImpl @Inject constructor() : AccountAndAssetAccountListItemMapper {

    override fun invoke(itemConfiguration: BaseItemConfiguration.AccountItemConfiguration): AccountListItem {
        return AccountListItem(itemConfiguration)
    }
}
