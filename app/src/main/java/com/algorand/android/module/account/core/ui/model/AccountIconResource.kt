package com.algorand.android.module.account.core.ui.model

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.algorand.android.R
import com.algorand.android.module.account.core.component.detail.domain.model.AccountType

enum class AccountIconResource(
    @DrawableRes val iconResId: Int,
    @ColorRes val backgroundColorResId: Int,
    @ColorRes val iconTintResId: Int
) {
    WATCH(R.drawable.ic_eye, R.color.wallet_1, R.color.wallet_1_icon),

    LEDGER(R.drawable.ic_ledger, R.color.wallet_3, R.color.wallet_3_icon),

    REKEYED(R.drawable.ic_ledger_rekeyed, R.color.wallet_3, R.color.wallet_3_icon),

    REKEYED_AUTH(R.drawable.ic_ledger_rekeyed, R.color.wallet_3, R.color.wallet_3_icon),

    STANDARD(R.drawable.ic_wallet, R.color.wallet_4, R.color.wallet_4_icon),

    PLACEHOLDER(R.drawable.ic_wallet, R.color.wallet_placeholder, R.color.wallet_placeholder_icon),

    UNDEFINED(R.drawable.ic_wallet, R.color.transparent, R.color.transparent);

    companion object {
        val DEFAULT_ACCOUNT_ICON_RESOURCE = UNDEFINED

        fun getAccountIconResourceByAccountType(accountType: AccountType?): AccountIconResource {
            return when (accountType) {
                AccountType.Algo25 -> STANDARD
                AccountType.LedgerBle -> LEDGER
                AccountType.Rekeyed -> REKEYED
                AccountType.RekeyedAuth -> REKEYED_AUTH
                AccountType.NoAuth -> WATCH
                null -> UNDEFINED
            }
        }

        fun getByName(name: String?): AccountIconResource {
            return entries.firstOrNull { it.name == name } ?: DEFAULT_ACCOUNT_ICON_RESOURCE
        }
    }
}
