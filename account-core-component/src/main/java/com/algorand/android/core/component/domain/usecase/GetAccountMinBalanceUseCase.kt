package com.algorand.android.core.component.domain.usecase

import com.algorand.android.accountinfo.component.domain.model.AccountInformation
import com.algorand.android.accountinfo.component.domain.usecase.GetAccountInformation
import com.algorand.android.core.utils.BalanceConstants.MIN_BALANCE
import com.algorand.android.core.utils.BalanceConstants.MIN_BALANCE_TO_KEEP_PER_APP_EXTRA_PAGES
import com.algorand.android.core.utils.BalanceConstants.MIN_BALANCE_TO_KEEP_PER_APP_TOTAL_SCHEMA_BYTE_SLICE
import com.algorand.android.core.utils.BalanceConstants.MIN_BALANCE_TO_KEEP_PER_APP_TOTAL_SCHEMA_INT
import com.algorand.android.core.utils.BalanceConstants.MIN_BALANCE_TO_KEEP_PER_CREATED_APPS
import com.algorand.android.core.utils.BalanceConstants.MIN_BALANCE_TO_KEEP_PER_OPTED_IN_APPS
import com.algorand.android.core.utils.BalanceConstants.MIN_BALANCE_TO_KEEP_PER_OPTED_IN_ASSET
import java.math.BigInteger
import javax.inject.Inject

internal class GetAccountMinBalanceUseCase @Inject constructor(
    private val getAccountInformation: GetAccountInformation
) : GetAccountMinBalance {

    override suspend fun invoke(accountAddress: String): BigInteger {
        val accountInformation = getAccountInformation(accountAddress) ?: return BigInteger.ZERO
        return invoke(accountInformation)
    }

    override suspend fun invoke(accountInformation: AccountInformation): BigInteger {
        return with(accountInformation) {
            calculateMinBalance(this, isRekeyed() || isThereAnyDifferentAsset() || isThereAnOptedInApp()).toBigInteger()
        }
    }

    private fun AccountInformation.isThereAnyDifferentAsset(): Boolean {
        return totalAssetsOptedIn > 0
    }

    private fun AccountInformation.isThereAnOptedInApp(): Boolean {
        return totalAppsOptedIn > 0
    }

    companion object {

        private fun calculateMinBalance(accountInformation: AccountInformation, includeMinBalance: Boolean): Long {
            with(accountInformation) {
                val optedAssets = totalAssetsOptedIn
                val createdApps = totalCreatedApps
                val optedApps = totalAppsOptedIn
                val intSchemeValue = appsTotalSchema?.numUint ?: 0
                val byteSchemeValue = appsTotalSchema?.numByteSlice ?: 0
                val extraAppPages = appsTotalExtraPages
                return MIN_BALANCE_TO_KEEP_PER_OPTED_IN_ASSET * optedAssets +
                    MIN_BALANCE_TO_KEEP_PER_CREATED_APPS * createdApps +
                    MIN_BALANCE_TO_KEEP_PER_OPTED_IN_APPS * optedApps +
                    MIN_BALANCE_TO_KEEP_PER_APP_TOTAL_SCHEMA_INT * intSchemeValue +
                    MIN_BALANCE_TO_KEEP_PER_APP_TOTAL_SCHEMA_BYTE_SLICE * byteSchemeValue +
                    MIN_BALANCE_TO_KEEP_PER_APP_EXTRA_PAGES * extraAppPages +
                    if (includeMinBalance) MIN_BALANCE else 0
            }
        }
    }
}
