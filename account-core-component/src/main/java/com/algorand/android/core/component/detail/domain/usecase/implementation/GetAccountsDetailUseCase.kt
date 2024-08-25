package com.algorand.android.core.component.detail.domain.usecase.implementation

import com.algorand.android.account.localaccount.domain.usecase.GetLocalAccounts
import com.algorand.android.core.component.detail.domain.model.AccountDetail
import com.algorand.android.core.component.detail.domain.usecase.*
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
