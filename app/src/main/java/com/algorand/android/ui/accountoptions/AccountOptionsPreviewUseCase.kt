package com.algorand.android.ui.accountoptions

import com.algorand.android.accountinfo.component.domain.usecase.GetAccountInformation
import com.algorand.android.core.component.detail.domain.model.AccountRegistrationType
import com.algorand.android.core.component.detail.domain.model.AccountType.Companion.canSignTransaction
import com.algorand.android.core.component.detail.domain.usecase.GetAccountDetail
import com.algorand.android.accountcore.ui.usecase.GetAccountDisplayName
import javax.inject.Inject

class AccountOptionsPreviewUseCase @Inject constructor(
    private val getAccountInformation: GetAccountInformation,
    private val getAccountDisplayName: GetAccountDisplayName,
    private val getAccountDetail: GetAccountDetail
) {

    suspend fun getPreview(address: String): AccountOptionsPreview? {
        val accountDetail = getAccountDetail(address)
        val canSignTransaction = accountDetail.accountType?.canSignTransaction() == true
        return getAccountInformation(address)?.run {
            AccountOptionsPreview(
                accountAddress = address,
                authAddress = rekeyAdminAddress,
                accountDisplayName = getAccountDisplayName(address),
                authAccountDisplayName = if (isRekeyed()) getAccountDisplayName(rekeyAdminAddress.orEmpty()) else null,
                isAuthAddressButtonVisible = isRekeyed(),
                isPassphraseButtonVisible = accountDetail.accountRegistrationType == AccountRegistrationType.Algo25,
                isUndoRekeyButtonVisible = isRekeyed() && canSignTransaction,
                canSignTransaction = canSignTransaction
            )
        }
    }
}
