package com.algorand.android.module.account.core.component.detail.domain.usecase.implementation

import com.algorand.android.module.account.info.domain.usecase.GetAccountInformationFlow
import com.algorand.android.module.asb.domain.usecase.GetAccountAsbBackUpStatus
import com.algorand.android.module.account.core.component.detail.domain.model.AccountDetail
import com.algorand.android.module.account.core.component.detail.domain.usecase.*
import com.algorand.android.custominfo.component.domain.usecase.GetCustomInfo
import kotlinx.coroutines.flow.*
import javax.inject.Inject

internal class GetAccountDetailFlowUseCase @Inject constructor(
    private val getAccountInformationFlow: GetAccountInformationFlow,
    private val getCustomInfo: GetCustomInfo,
    private val getAccountType: GetAccountType,
    private val getAccountRegistrationType: GetAccountRegistrationType,
    private val getAccountAsbBackUpStatus: GetAccountAsbBackUpStatus
) : GetAccountDetailFlow {

    override fun invoke(address: String): Flow<AccountDetail?> {
        return getAccountInformationFlow(address).map {
            if (it == null) return@map null
            AccountDetail(
                address = address,
                customInfo = getCustomInfo(address),
                accountRegistrationType = getAccountRegistrationType(address),
                accountType = getAccountType(address),
                isBackedUp = getAccountAsbBackUpStatus(address)
            )
        }
    }
}
