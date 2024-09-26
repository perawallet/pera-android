package com.algorand.android.module.account.core.component.detail.domain.usecase.implementation

import com.algorand.android.module.asb.domain.usecase.GetAccountAsbBackUpStatus
import com.algorand.android.module.account.core.component.detail.domain.model.AccountDetail
import com.algorand.android.module.account.core.component.detail.domain.usecase.*
import com.algorand.android.module.custominfo.domain.usecase.GetCustomInfo
import javax.inject.Inject

internal class GetAccountDetailUseCase @Inject constructor(
    private val getAccountType: GetAccountType,
    private val getAccountRegistrationType: GetAccountRegistrationType,
    private val getCustomInfo: GetCustomInfo,
    private val getAccountAsbBackUpStatus: GetAccountAsbBackUpStatus
) : GetAccountDetail {

    override suspend operator fun invoke(address: String): AccountDetail {
        val customInfo = getCustomInfo(address)
        return AccountDetail(
            address = address,
            customInfo = customInfo,
            accountRegistrationType = getAccountRegistrationType(address),
            accountType = getAccountType(address),
            isBackedUp = getAccountAsbBackUpStatus(address)
        )
    }
}
