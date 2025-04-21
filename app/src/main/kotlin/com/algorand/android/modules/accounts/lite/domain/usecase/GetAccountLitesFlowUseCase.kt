/*
 * Copyright 2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.modules.accounts.lite.domain.usecase

import com.algorand.android.modules.accounts.lite.domain.model.AccountLite
import com.algorand.android.modules.parity.domain.model.AlgoAmountValue
import com.algorand.android.modules.parity.domain.usecase.GetAlgoAmountValue
import com.algorand.android.modules.parity.domain.usecase.GetPrimaryCurrencyAssetParityValue
import com.algorand.android.modules.parity.domain.usecase.GetSecondaryCurrencyAssetParityValue
import com.algorand.android.utils.toShortenedAddress
import com.algorand.wallet.account.custom.domain.model.CustomAccountInfo
import com.algorand.wallet.account.custom.domain.usecase.GetAccountsCustomInfoFlow
import com.algorand.wallet.account.detail.domain.usecase.GetAccountRegistrationType
import com.algorand.wallet.account.detail.domain.usecase.GetAccountType
import com.algorand.wallet.account.lite.domain.model.AccountLiteInformation
import com.algorand.wallet.account.lite.domain.model.AssetHoldingLite
import com.algorand.wallet.account.lite.domain.usecase.GetAccountsLiteInformationFlow
import com.algorand.wallet.account.lite.domain.usecase.GetAssetHoldingsLiteFlow
import com.algorand.wallet.account.local.domain.model.LocalAccount
import com.algorand.wallet.asset.lite.domain.model.AssetLiteInformation
import com.algorand.wallet.asset.lite.domain.usecase.GetAssetsLiteInformationFlow
import java.math.BigDecimal
import java.math.BigInteger
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

internal class GetAccountLitesFlowUseCase @Inject constructor(
    private val getAccountsLiteInformationFlow: GetAccountsLiteInformationFlow,
    private val getAssetHoldingsLiteFlow: GetAssetHoldingsLiteFlow,
    private val getAssetsLiteInformationFlow: GetAssetsLiteInformationFlow,
    private val getAccountsCustomInfoFlow: GetAccountsCustomInfoFlow,
    private val getAccountType: GetAccountType,
    private val getAlgoAmountValue: GetAlgoAmountValue,
    private val getPrimaryCurrencyAssetParityValue: GetPrimaryCurrencyAssetParityValue,
    private val getSecondaryCurrencyAssetParityValue: GetSecondaryCurrencyAssetParityValue,
    private val getAccountRegistrationType: GetAccountRegistrationType
) : GetAccountLitesFlow {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun invoke(localAccounts: List<LocalAccount>, addresses: List<String>): Flow<Map<String, AccountLite>> {
        return combine(
            getAccountsLiteInformationFlow(addresses),
            getAssetHoldingsLiteFlow(addresses),
            getAccountsCustomInfoFlow(addresses),
        ) { accountsLite, assetHoldingsLite, customInfos ->
            AccountsFlowData(accountsLite, assetHoldingsLite, localAccounts, customInfos)
        }
            .flatMapLatest(::mergeAccountsFlowDataAndAssetLiteInfo)
            .map(::getAccountLites)
    }

    private fun mergeAccountsFlowDataAndAssetLiteInfo(
        accountsFlowData: AccountsFlowData
    ): Flow<Pair<AccountsFlowData, Map<Long, AssetLiteInformation?>>> {
        val assetIds = accountsFlowData.assetHoldingsLite.values
            .map { it.assetHoldingAmounts.keys }
            .flatten()
            .distinct()
        return getAssetsLiteInformationFlow(assetIds).map { assetsLiteInformation ->
            Pair(accountsFlowData, assetsLiteInformation)
        }
    }

    private suspend fun getAccountLites(
        flowDataAssetLiteInfoPair: Pair<AccountsFlowData, Map<Long, AssetLiteInformation?>>
    ): Map<String, AccountLite> {
        val (accountsFlowData, assetLiteInfo) = flowDataAssetLiteInfoPair
        return with(accountsFlowData) {
            val accountsPayloads = getAccountsPayloads(accountsLite, assetHoldingsLite, assetLiteInfo)
            createAccountLites(accountsPayloads, localAccounts, customNames)
        }
    }

    private suspend fun createAccountLites(
        accountPayload: Map<String, AccountPayload>,
        localAccounts: List<LocalAccount>,
        customInfo: Map<String, CustomAccountInfo?>
    ): Map<String, AccountLite> {
        return withContext(Dispatchers.Default) {
            accountPayload.map { (address, accountsLiteCombined) ->
                async {
                    val localAccount = localAccounts.first { it.algoAddress == address }
                    val customAccountInfo = customInfo[address]
                    address to AccountLite(
                        address = address,
                        registrationType = getAccountRegistrationType(localAccount),
                        customName = customAccountInfo?.customName ?: address.toShortenedAddress(),
                        isBackedUp = customAccountInfo?.isBackedUp ?: false,
                        sortIndex = customAccountInfo?.orderIndex ?: Int.MAX_VALUE,
                        cachedInfo = getCachedInfo(localAccounts, address, accountsLiteCombined)
                    )
                }
            }.awaitAll().associate {
                it.first to it.second
            }
        }
    }

    private fun getCachedInfo(
        localAccounts: List<LocalAccount>,
        address: String,
        accountPayload: AccountPayload
    ): AccountLite.CachedInfo? {
        if (accountPayload.accountInfoLiteInformation == null) {
            return null
        }
        val rekeyAuthAddress = accountPayload.accountInfoLiteInformation.rekeyAuthAddress
        val accountType = getAccountType(address, rekeyAuthAddress, localAccounts) ?: return null

        val algoBalance = accountPayload.accountInfoLiteInformation.algoBalance
        val algoAmountValue = getAlgoAmountValue(algoBalance)
        val (primaryAccountValue, secondaryAccountValue) = getPrimaryAndSecondaryAccountValues(
            accountPayload.assetHoldingLiteInformation,
            algoAmountValue
        )
        return AccountLite.CachedInfo(
            type = accountType,
            algoAmountValue = algoAmountValue,
            primaryAccountValue = primaryAccountValue,
            secondaryAccountValue = secondaryAccountValue,
            assetCount = accountPayload.assetHoldingLiteInformation.size
        )
    }

    private fun getPrimaryAndSecondaryAccountValues(
        assetHoldingLiteInformation: Map<Long, AssetHoldingLiteInformation?>,
        algoAmountValue: AlgoAmountValue
    ): Pair<BigDecimal, BigDecimal> {
        var primary = algoAmountValue.parityValueInSelectedCurrency.amountAsCurrency
        var secondary = algoAmountValue.parityValueInSecondaryCurrency.amountAsCurrency
        assetHoldingLiteInformation.values.forEach {
            if (it?.usdValue == null) return@forEach
            primary += getPrimaryCurrencyAssetParityValue(it.amount, it.usdValue, it.decimals).amountAsCurrency
            secondary += getSecondaryCurrencyAssetParityValue(it.amount, it.usdValue, it.decimals).amountAsCurrency
        }
        return primary to secondary
    }

    private suspend fun getAccountsPayloads(
        accountsLite: Map<String, AccountLiteInformation?>,
        assetHoldingsLite: Map<String, AssetHoldingLite>,
        assetLiteInfo: Map<Long, AssetLiteInformation?>
    ): Map<String, AccountPayload> {
        val accountLiteInformationMap = mutableMapOf<String, AccountPayload>()
        withContext(Dispatchers.Default) {
            accountsLite.map { (address, accountLiteInformation) ->
                async {
                    accountLiteInformationMap[address] = AccountPayload(
                        address = address,
                        accountInfoLiteInformation = accountLiteInformation,
                        assetHoldingLiteInformation = getAssetHoldingLiteInformation(
                            address,
                            assetHoldingsLite,
                            assetLiteInfo
                        )
                    )
                }
            }.awaitAll()
        }
        return accountLiteInformationMap
    }

    private fun getAssetHoldingLiteInformation(
        address: String,
        assetHoldingsLite: Map<String, AssetHoldingLite>,
        assetLiteInfo: Map<Long, AssetLiteInformation?>
    ): Map<Long, AssetHoldingLiteInformation> {
        val assetHoldingLites = assetHoldingsLite[address] ?: return emptyMap()
        val assetHoldingLiteInformationMap = mutableMapOf<Long, AssetHoldingLiteInformation>()
        assetHoldingLites.assetHoldingAmounts.forEach { (id, assetHoldingAmount) ->
            val assetLiteInformation = assetLiteInfo[id] ?: return@forEach
            val assetHoldingLiteInformation = AssetHoldingLiteInformation(
                id = id,
                usdValue = assetLiteInformation.usdValue,
                decimals = assetLiteInformation.decimals,
                amount = assetHoldingAmount
            )
            assetHoldingLiteInformationMap[id] = assetHoldingLiteInformation
        }

        return assetHoldingLiteInformationMap
    }
}

private data class AccountsFlowData(
    val accountsLite: Map<String, AccountLiteInformation?>,
    val assetHoldingsLite: Map<String, AssetHoldingLite>,
    val localAccounts: List<LocalAccount>,
    val customNames: Map<String, CustomAccountInfo?>
)

private data class AccountPayload(
    val address: String,
    val accountInfoLiteInformation: AccountLiteInformation?,
    val assetHoldingLiteInformation: Map<Long, AssetHoldingLiteInformation?>
)

private data class AssetHoldingLiteInformation(
    val id: Long,
    val usdValue: BigDecimal?,
    val decimals: Int,
    val amount: BigInteger
)
