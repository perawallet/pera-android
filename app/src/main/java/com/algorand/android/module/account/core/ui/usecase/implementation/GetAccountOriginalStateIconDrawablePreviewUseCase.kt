package com.algorand.android.module.account.core.ui.usecase.implementation

import com.algorand.android.R
import com.algorand.android.module.account.core.component.detail.domain.model.AccountDetail
import com.algorand.android.module.account.core.component.detail.domain.model.AccountType
import com.algorand.android.module.account.core.component.detail.domain.usecase.GetAccountDetail
import com.algorand.android.module.account.core.ui.model.AccountIconDrawablePreview
import com.algorand.android.module.account.core.ui.model.AccountIconResource
import com.algorand.android.module.account.core.ui.model.AccountIconResource.STANDARD
import com.algorand.android.module.account.core.ui.usecase.GetAccountOriginalStateIconDrawablePreview
import javax.inject.Inject

internal class GetAccountOriginalStateIconDrawablePreviewUseCase @Inject constructor(
    private val getAccountDetail: GetAccountDetail
) : GetAccountOriginalStateIconDrawablePreview {
    override suspend fun invoke(address: String): AccountIconDrawablePreview {
        val accountDetail = getAccountDetail(address)
        val accountIconResId = getAccountIconResId(accountDetail)
        val accountIconTintResId = getAccountIconTintResId(accountDetail)
        val accountIconBackgroundColorResId = getAccountIconBackgroundColorResId(accountDetail)
        return AccountIconDrawablePreview(
            backgroundColorResId = accountIconBackgroundColorResId,
            iconResId = accountIconResId,
            iconTintResId = accountIconTintResId
        )
    }

    private fun getAccountIconBackgroundColorResId(accountDetail: AccountDetail?): Int {
        return when (accountDetail?.accountType) {
            AccountType.LedgerBle -> AccountIconResource.LEDGER.backgroundColorResId
            AccountType.NoAuth -> AccountIconResource.WATCH.backgroundColorResId
            AccountType.Algo25 -> STANDARD.backgroundColorResId
            AccountType.RekeyedAuth, AccountType.Rekeyed, null -> {
                if (accountDetail?.canSignTransaction() == true) {
                    STANDARD.backgroundColorResId
                } else {
                    R.color.layer_gray_lighter
                }
            }
        }
    }

    private fun getAccountIconTintResId(accountDetail: AccountDetail?): Int {
        return when (accountDetail?.accountType) {
            AccountType.LedgerBle -> AccountIconResource.LEDGER.iconTintResId
            AccountType.NoAuth -> AccountIconResource.WATCH.iconTintResId
            AccountType.Algo25 -> STANDARD.iconTintResId
            AccountType.RekeyedAuth, AccountType.Rekeyed, null -> {
                if (accountDetail?.canSignTransaction() == true) STANDARD.iconTintResId else R.color.text_gray_lighter
            }
        }
    }

    private fun getAccountIconResId(accountDetail: AccountDetail?): Int {
        return when (accountDetail?.accountType) {
            AccountType.LedgerBle -> AccountIconResource.LEDGER.iconResId
            AccountType.NoAuth -> AccountIconResource.WATCH.iconResId
            AccountType.Algo25 -> STANDARD.iconResId
            AccountType.RekeyedAuth, AccountType.Rekeyed, null -> {
                if (accountDetail?.canSignTransaction() == true) STANDARD.iconResId else R.drawable.ic_question
            }
        }
    }
}
