package com.algorand.android.module.account.core.ui.usecase.implementation

import com.algorand.android.module.account.core.ui.model.AccountIconDrawablePreview
import com.algorand.android.module.account.core.ui.usecase.GetAccountIconDrawablePreview
import com.algorand.android.module.account.core.component.detail.domain.model.AccountDetail
import com.algorand.android.module.account.core.component.detail.domain.model.AccountType
import com.algorand.android.module.account.core.component.detail.domain.usecase.GetAccountDetail
import com.algorand.android.R
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
            AccountType.Algo25 -> com.algorand.android.module.account.core.ui.model.AccountIconResource.STANDARD.backgroundColorResId
            AccountType.LedgerBle -> com.algorand.android.module.account.core.ui.model.AccountIconResource.LEDGER.backgroundColorResId
            AccountType.Rekeyed -> R.color.negative_lighter
            AccountType.RekeyedAuth -> com.algorand.android.module.account.core.ui.model.AccountIconResource.REKEYED.backgroundColorResId
            AccountType.NoAuth -> com.algorand.android.module.account.core.ui.model.AccountIconResource.WATCH.backgroundColorResId
            null -> R.color.layer_gray_lighter
        }
    }

    private fun getAccountIconTintResId(accountType: AccountType?): Int {
        return when (accountType) {
            AccountType.Algo25 -> com.algorand.android.module.account.core.ui.model.AccountIconResource.STANDARD.iconTintResId
            AccountType.Rekeyed -> R.color.negative
            AccountType.LedgerBle -> com.algorand.android.module.account.core.ui.model.AccountIconResource.LEDGER.iconTintResId
            AccountType.RekeyedAuth -> com.algorand.android.module.account.core.ui.model.AccountIconResource.LEDGER.iconTintResId
            AccountType.NoAuth -> com.algorand.android.module.account.core.ui.model.AccountIconResource.WATCH.iconTintResId
            null -> R.color.text_gray
        }
    }

    private fun getAccountIconResId(accountType: AccountType?): Int {
        return when (accountType) {
            AccountType.Algo25 -> com.algorand.android.module.account.core.ui.model.AccountIconResource.STANDARD.iconResId
            AccountType.LedgerBle -> com.algorand.android.module.account.core.ui.model.AccountIconResource.LEDGER.iconResId
            AccountType.NoAuth -> com.algorand.android.module.account.core.ui.model.AccountIconResource.WATCH.iconResId
            AccountType.Rekeyed, AccountType.RekeyedAuth -> R.drawable.ic_rekey_shield
            null -> com.algorand.android.module.account.core.ui.model.AccountIconResource.STANDARD.iconResId
        }
    }
}
