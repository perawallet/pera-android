package com.algorand.android.module.account.core.ui.accountsorting.domain.usecase.accountlistitemsorter

import com.algorand.android.module.account.core.ui.accountsorting.domain.model.AccountAndAssetListItem
import com.algorand.android.module.account.core.ui.accountsorting.domain.model.BaseAccountAndAssetListItem
import com.algorand.android.module.account.core.ui.model.AccountDisplayName
import java.math.BigDecimal

internal interface AccountListItemSorter {
    fun sortBaseAccountAndAssetListItem(
        currentList: List<BaseAccountAndAssetListItem.AccountListItem>
    ): List<BaseAccountAndAssetListItem.AccountListItem>

    fun sortAccountAndAssetListItem(
        currentList: List<AccountAndAssetListItem.AccountListItem>
    ): List<AccountAndAssetListItem.AccountListItem>

    fun sortMap(accountNameAndValueMap: Map<AccountDisplayName, BigDecimal>): Map<AccountDisplayName, BigDecimal>
}
