package com.algorand.android.module.account.info.domain.usecase.implementation

import com.algorand.android.module.account.info.domain.usecase.*
import java.math.BigInteger
import javax.inject.Inject

internal class IsAssetOwnedByAccountUseCase @Inject constructor(
    private val getAccountInformation: GetAccountInformation
) : IsAssetOwnedByAccount {

    override suspend operator fun invoke(address: String, assetId: Long): Boolean {
        val assetHolding = getAccountInformation(address)
            ?.assetHoldings
            ?.firstOrNull { it.assetId == assetId }
            ?: return false
        return assetHolding.amount > BigInteger.ZERO
    }
}
