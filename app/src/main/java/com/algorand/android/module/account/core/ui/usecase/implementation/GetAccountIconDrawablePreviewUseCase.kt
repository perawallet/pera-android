package com.algorand.android.module.account.core.ui.usecase.implementation

import com.algorand.android.R
import com.algorand.android.module.account.core.component.detail.domain.model.AccountDetail
import com.algorand.android.module.account.core.component.detail.domain.model.AccountType
import com.algorand.android.module.account.core.component.detail.domain.usecase.GetAccountDetail
import com.algorand.android.module.account.core.ui.model.AccountIconDrawablePreview
import com.algorand.android.module.account.core.ui.model.AccountIconResource
import com.algorand.android.module.account.core.ui.usecase.GetAccountIconDrawablePreview
import javax.inject.Inject

internal class GetAccountIconDrawablePreviewUseCase @Inject constructor(
    private val getAccountDetail: GetAccountDetail
) : GetAccountIconDrawablePreview {

    override suspend fun invoke(address: String): AccountIconDrawablePreview {
        val accountDetail = getAccountDetail(address)
        return getAccountIconDrawablePreview(accountDetail)
    }

    override suspend fun invoke(accountDetail: AccountDetail): AccountIconDrawablePreview {
        return getAccountIconDrawablePreview(accountDetail)
    }

    private fun getAccountIconDrawablePreview(accountDetail: AccountDetail): AccountIconDrawablePreview {
        val accountIconResId = getAccountIconResId(accountDetail.accountType)
        val accountIconTintResId = getAccountIconTintResId(accountDetail.accountType)
        val accountIconBackgroundColorResId = getAccountIconBackgroundColorResId(accountDetail.accountType)
        return AccountIconDrawablePreview(
            backgroundColorResId = accountIconBackgroundColorResId,
            iconResId = accountIconResId,
            iconTintResId = accountIconTintResId
        )
    }

    private fun getAccountIconBackgroundColorResId(accountType: AccountType?): Int {
        return when (accountType) {
            AccountType.Algo25 -> AccountIconResource.STANDARD.backgroundColorResId
            AccountType.LedgerBle -> AccountIconResource.LEDGER.backgroundColorResId
            AccountType.Rekeyed -> R.color.negative_lighter
            AccountType.RekeyedAuth -> AccountIconResource.REKEYED.backgroundColorResId
            AccountType.NoAuth -> AccountIconResource.WATCH.backgroundColorResId
            null -> R.color.layer_gray_lighter
        }
    }

    private fun getAccountIconTintResId(accountType: AccountType?): Int {
        return when (accountType) {
            AccountType.Algo25 -> AccountIconResource.STANDARD.iconTintResId
            AccountType.Rekeyed -> R.color.negative
            AccountType.LedgerBle -> AccountIconResource.LEDGER.iconTintResId
            AccountType.RekeyedAuth -> AccountIconResource.LEDGER.iconTintResId
            AccountType.NoAuth -> AccountIconResource.WATCH.iconTintResId
            null -> R.color.text_gray
        }
    }

    private fun getAccountIconResId(accountType: AccountType?): Int {
        return when (accountType) {
            AccountType.Algo25 -> AccountIconResource.STANDARD.iconResId
            AccountType.LedgerBle -> AccountIconResource.LEDGER.iconResId
            AccountType.NoAuth -> AccountIconResource.WATCH.iconResId
            AccountType.Rekeyed, AccountType.RekeyedAuth -> R.drawable.ic_rekey_shield
            null -> AccountIconResource.STANDARD.iconResId
        }
    }
}
