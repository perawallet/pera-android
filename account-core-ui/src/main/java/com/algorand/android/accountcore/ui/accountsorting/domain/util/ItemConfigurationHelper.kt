package com.algorand.android.accountcore.ui.accountsorting.domain.util

import com.algorand.android.accountcore.ui.model.BaseItemConfiguration
import com.algorand.android.core.component.detail.domain.model.AccountDetail

internal object ItemConfigurationHelper {

    suspend fun configureListItem(
        accountDetail: AccountDetail,
        accountAddress: String,
        onLoadedAccountConfiguration: suspend AccountDetail.() -> BaseItemConfiguration.AccountItemConfiguration,
        onFailedAccountConfiguration: suspend String.() -> BaseItemConfiguration.AccountItemConfiguration?
    ): BaseItemConfiguration.AccountItemConfiguration? {
        return if (accountDetail.accountType != null) {
            onLoadedAccountConfiguration(accountDetail)
        } else {
            onFailedAccountConfiguration.invoke((accountAddress))
        }
    }
}