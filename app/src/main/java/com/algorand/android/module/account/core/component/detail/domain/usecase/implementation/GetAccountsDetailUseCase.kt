package com.algorand.android.module.account.core.component.detail.domain.usecase.implementation

import com.algorand.android.module.account.core.component.detail.domain.model.AccountDetail
import com.algorand.android.module.account.core.component.detail.domain.usecase.GetAccountDetail
import com.algorand.android.module.account.core.component.detail.domain.usecase.GetAccountsDetail
import com.algorand.android.module.account.local.domain.usecase.GetLocalAccounts
import javax.inject.Inject

internal class GetAccountsDetailUseCase @Inject constructor(
    private val getAccountDetail: GetAccountDetail,
    private val getLocalAccounts: GetLocalAccounts
) : GetAccountsDetail {

    override suspend fun invoke(): List<AccountDetail> {
        return getLocalAccounts().map {
            getAccountDetail(it.address)
        }
    }
}
