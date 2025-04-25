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

package com.algorand.android.modules.onboarding.recoverypassphrase.rekeyedaccountselection.information.ui.usecase

import com.algorand.android.R
import com.algorand.android.decider.AssetDrawableProviderDecider
import com.algorand.android.models.BaseAccountAssetData
import com.algorand.android.modules.accountcore.domain.usecase.GetAccountBaseOwnedAssetData
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountDisplayName
import com.algorand.android.modules.accounticon.ui.mapper.AccountIconDrawablePreviewMapper
import com.algorand.android.modules.basefoundaccount.information.ui.mapoer.BaseFoundAccountInformationItemMapper
import com.algorand.android.modules.basefoundaccount.information.ui.model.BaseFoundAccountInformationItem
import com.algorand.android.modules.basefoundaccount.information.ui.usecase.BaseFoundAccountInformationItemUseCase
import com.algorand.android.modules.onboarding.recoverypassphrase.rekeyedaccountselection.information.ui.mapper.RekeyedAccountInformationPreviewMapper
import com.algorand.android.modules.onboarding.recoverypassphrase.rekeyedaccountselection.information.ui.model.RekeyedAccountInformationPreview
import com.algorand.android.modules.parity.domain.usecase.ParityUseCase
import com.algorand.android.modules.verificationtier.ui.decider.VerificationTierConfigurationDecider
import com.algorand.android.utils.AssetName
import com.algorand.android.utils.extensions.mapNotBlank
import com.algorand.android.utils.formatAsCurrency
import com.algorand.android.utils.toShortenedAddress
import com.algorand.wallet.account.core.domain.usecase.FetchAccountInformationAndCacheAssets
import com.algorand.wallet.account.detail.domain.model.AccountType
import com.algorand.wallet.account.info.domain.model.AccountInformation
import com.algorand.wallet.account.info.domain.usecase.FetchRekeyedAccounts
import com.algorand.wallet.asset.domain.util.AssetConstants.ALGO_ID
import com.algorand.wallet.foundation.PeraResult
import java.math.BigDecimal
import javax.inject.Inject

@SuppressWarnings("LongParameterList")
class RekeyedAccountInformationPreviewUseCase @Inject constructor(
    private val rekeyedAccountInformationPreviewMapper: RekeyedAccountInformationPreviewMapper,
    private val verificationTierConfigurationDecider: VerificationTierConfigurationDecider,
    private val assetDrawableProviderDecider: AssetDrawableProviderDecider,
    private val parityUseCase: ParityUseCase,
    private val accountIconDrawablePreviewMapper: AccountIconDrawablePreviewMapper,
    private val fetchRekeyedAccounts: FetchRekeyedAccounts,
    private val getAccountBaseOwnedAssetData: GetAccountBaseOwnedAssetData,
    private val fetchAccountInformationAndCacheAssets: FetchAccountInformationAndCacheAssets,
    private val getAccountDisplayName: GetAccountDisplayName,
    baseFoundAccountInformationItemMapper: BaseFoundAccountInformationItemMapper
) : BaseFoundAccountInformationItemUseCase(baseFoundAccountInformationItemMapper) {

    fun getInitialRekeyedAccountInformationPreview(): RekeyedAccountInformationPreview {
        return rekeyedAccountInformationPreviewMapper.mapToRekeyedAccountInformationPreview(
            isLoading = true,
            foundAccountInformationItemList = emptyList()
        )
    }

    suspend fun getRekeyedAccountInformationPreviewFlow(
        accountAddress: String,
        preview: RekeyedAccountInformationPreview
    ): PeraResult<RekeyedAccountInformationPreview> {
        return fetchAccountInformationAndCacheAssets(
            address = accountAddress,
            includeClosedAccount = false
        ).map { accountInformation ->
            lateinit var foundAccountInformationItemList: List<BaseFoundAccountInformationItem>
            fetchRekeyedAccounts(accountAddress).use(
                onSuccess = { rekeyedAccountInformation ->
                    foundAccountInformationItemList = createBaseFoundAccountInformationItemList(
                        accountInformation = accountInformation,
                        rekeyedAccounts = rekeyedAccountInformation
                    )
                },
                onFailed = { _, _ ->
                    foundAccountInformationItemList = createBaseFoundAccountInformationItemList(
                        accountInformation = accountInformation,
                        rekeyedAccounts = emptyList()
                    )
                }
            )
            preview.copy(
                isLoading = false,
                foundAccountInformationItemList = foundAccountInformationItemList
            )
        }
    }

    private suspend fun createBaseFoundAccountInformationItemList(
        accountInformation: AccountInformation,
        rekeyedAccounts: List<AccountInformation>
    ): List<BaseFoundAccountInformationItem> {
        var primaryAccountValue = BigDecimal.ZERO
        var secondaryAccountValue = BigDecimal.ZERO
        val accountAssetDataList = accountInformation.assetHoldings.mapNotNull {
            getAccountBaseOwnedAssetData(accountInformation.address, it.assetId)
        }
        val algoAssetItem = getAccountBaseOwnedAssetData(accountInformation.address, ALGO_ID)?.run {
            createAssetItem(
                baseAccountAssetData = this,
                onCalculationDone = { primaryValue, secondaryValue ->
                    primaryAccountValue += primaryValue
                    secondaryAccountValue += secondaryValue
                }
            )
        }

        val assetItemList = createAssetListItems(
            accountAssetData = accountAssetDataList,
            onCalculationDone = { primaryValue, secondaryValue ->
                primaryAccountValue += primaryValue
                secondaryAccountValue += secondaryValue
            }
        )

        val accountItem = createAccountItem(
            accountInformation = accountInformation,
            primaryAccountValue = primaryAccountValue,
            secondaryAccountValue = secondaryAccountValue
        )

        val authAccountItem = crateAuthAccount(accountInformation.rekeyAdminAddress)

        val rekeyedAccountItemList = rekeyedAccounts.map { it.address }.run { createRekeyedAccounts(this) }
        return mutableListOf<BaseFoundAccountInformationItem>().apply {
            add(createTitleItem(R.string.account_details))
            add(accountItem)

            add(createTitleItem(R.string.assets))
            if (algoAssetItem != null) {
                add(algoAssetItem)
            }
            addAll(assetItemList)

            if (authAccountItem != null) {
                add(createTitleItem(R.string.can_be_signed_by))
                add(authAccountItem)
            }

            if (rekeyedAccountItemList.isNotEmpty()) {
                add(createTitleItem(R.string.can_sign_for_these))
                addAll(rekeyedAccountItemList)
            }
        }
    }

    private fun createAssetListItems(
        accountAssetData: List<BaseAccountAssetData>,
        onCalculationDone: (BigDecimal, BigDecimal) -> Unit
    ): List<BaseFoundAccountInformationItem.AssetItem> {
        var primaryAssetsValue = BigDecimal.ZERO
        var secondaryAssetsValue = BigDecimal.ZERO
        return mutableListOf<BaseFoundAccountInformationItem.AssetItem>().apply {
            accountAssetData.forEach { accountAssetData ->
                val assetItem = createAssetItem(
                    baseAccountAssetData = accountAssetData,
                    onCalculationDone = { primaryValue, secondaryValue ->
                        primaryAssetsValue += primaryValue
                        secondaryAssetsValue += secondaryValue
                    }
                )
                if (assetItem != null) {
                    add(assetItem)
                }
            }
        }.also { onCalculationDone.invoke(primaryAssetsValue, secondaryAssetsValue) }
    }

    private fun createAssetItem(
        baseAccountAssetData: BaseAccountAssetData,
        onCalculationDone: (BigDecimal, BigDecimal) -> Unit
    ): BaseFoundAccountInformationItem.AssetItem? {
        return (baseAccountAssetData as? BaseAccountAssetData.BaseOwnedAssetData)?.run {
            createAssetItem(
                assetId = id,
                name = AssetName.create(name),
                shortName = AssetName.createShortName(shortName),
                verificationTierConfiguration = verificationTierConfigurationDecider
                    .decideVerificationTierConfiguration(verificationTier),
                baseAssetDrawableProvider = assetDrawableProviderDecider.getAssetDrawableProvider(this),
                formattedPrimaryValue = parityValueInSelectedCurrency.getFormattedCompactValue(),
                formattedSecondaryValue = parityValueInSecondaryCurrency.getFormattedCompactValue()
            ).also {
                onCalculationDone.invoke(
                    parityValueInSelectedCurrency.amountAsCurrency,
                    parityValueInSecondaryCurrency.amountAsCurrency
                )
            }
        }
    }

    private suspend fun createAccountItem(
        accountInformation: AccountInformation,
        primaryAccountValue: BigDecimal,
        secondaryAccountValue: BigDecimal
    ): BaseFoundAccountInformationItem.AccountItem {
        val selectedCurrencySymbol = parityUseCase.getPrimaryCurrencySymbolOrName()
        val secondaryCurrencySymbol = parityUseCase.getSecondaryCurrencySymbol()
        return createAccountItem(
            accountDisplayName = getAccountDisplayName(
                address = accountInformation.address,
                name = accountInformation.address.toShortenedAddress(),
                type = AccountType.Rekeyed
            ),
            accountIconDrawablePreview = accountIconDrawablePreviewMapper.mapToAccountIconDrawablePreview(
                backgroundColorResId = R.color.wallet_4,
                iconTintResId = R.color.wallet_4_icon,
                iconResId = R.drawable.ic_rekey_shield
            ),
            formattedSecondaryValue = primaryAccountValue.formatAsCurrency(selectedCurrencySymbol),
            formattedPrimaryValue = secondaryAccountValue.formatAsCurrency(secondaryCurrencySymbol),
        )
    }

    private suspend fun crateAuthAccount(rekeyAdminAddress: String?): BaseFoundAccountInformationItem.AccountItem? {
        return rekeyAdminAddress?.mapNotBlank { safeRekeyAdminAddress ->
            createAccountItem(
                accountDisplayName = getAccountDisplayName(
                    address = safeRekeyAdminAddress,
                    name = safeRekeyAdminAddress.toShortenedAddress(),
                    type = AccountType.Algo25
                ),
                accountIconDrawablePreview = accountIconDrawablePreviewMapper.mapToAccountIconDrawablePreview(
                    backgroundColorResId = R.color.wallet_4,
                    iconTintResId = R.color.wallet_4_icon,
                    iconResId = R.drawable.ic_wallet
                ),
                formattedSecondaryValue = null,
                formattedPrimaryValue = null
            )
        }
    }

    private suspend fun createRekeyedAccounts(
        rekeyedAccountAddresses: List<String>
    ): List<BaseFoundAccountInformationItem.AccountItem> {
        return rekeyedAccountAddresses.map { rekeyedAccountAddress ->
            createAccountItem(
                accountDisplayName = getAccountDisplayName(
                    address = rekeyedAccountAddress,
                    name = rekeyedAccountAddress.toShortenedAddress(),
                    type = AccountType.Rekeyed
                ),
                accountIconDrawablePreview = accountIconDrawablePreviewMapper.mapToAccountIconDrawablePreview(
                    backgroundColorResId = R.color.wallet_4,
                    iconTintResId = R.color.wallet_4_icon,
                    iconResId = R.drawable.ic_rekey_shield
                ),
                formattedSecondaryValue = null,
                formattedPrimaryValue = null
            )
        }
    }
}
