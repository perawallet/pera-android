/*
 * Copyright 2022 Pera Wallet, LDA
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
import com.algorand.android.accountcore.ui.asset.assetdrawable.GetAssetDrawableProvider
import com.algorand.android.accountcore.ui.mapper.VerificationTierConfigurationMapper
import com.algorand.android.accountcore.ui.model.AccountIconDrawablePreview
import com.algorand.android.accountcore.ui.usecase.GetAccountDisplayName
import com.algorand.android.accountcore.ui.usecase.GetAssetName
import com.algorand.android.accountinfo.component.domain.model.AccountInformation
import com.algorand.android.accountinfo.component.domain.usecase.FetchRekeyedAccounts
import com.algorand.android.assetdetail.component.AssetConstants.ALGO_ASSET_ID
import com.algorand.android.core.component.caching.domain.usecase.FetchAccountInformationAndCacheAssets
import com.algorand.android.core.component.domain.model.BaseAccountAssetData
import com.algorand.android.core.component.assetdata.usecase.GetAccountBaseOwnedAssetData
import com.algorand.android.modules.basefoundaccount.information.ui.mapoer.BaseFoundAccountInformationItemMapper
import com.algorand.android.modules.basefoundaccount.information.ui.model.BaseFoundAccountInformationItem
import com.algorand.android.modules.basefoundaccount.information.ui.usecase.BaseFoundAccountInformationItemUseCase
import com.algorand.android.modules.onboarding.recoverypassphrase.rekeyedaccountselection.information.ui.mapper.RekeyedAccountInformationPreviewMapper
import com.algorand.android.modules.onboarding.recoverypassphrase.rekeyedaccountselection.information.ui.model.RekeyedAccountInformationPreview
import com.algorand.android.parity.domain.usecase.primary.GetPrimaryCurrencySymbolOrName
import com.algorand.android.parity.domain.usecase.secondary.GetSecondaryCurrencySymbol
import com.algorand.android.utils.extensions.mapNotBlank
import com.algorand.android.utils.formatAsCurrency
import java.math.BigDecimal
import javax.inject.Inject
import kotlinx.coroutines.flow.flow

@SuppressWarnings("LongParameterList")
class RekeyedAccountInformationPreviewUseCase @Inject constructor(
    private val rekeyedAccountInformationPreviewMapper: RekeyedAccountInformationPreviewMapper,
    private val verificationTierConfigurationMapper: VerificationTierConfigurationMapper,
    private val getAccountBaseOwnedAssetData: GetAccountBaseOwnedAssetData,
    private val getPrimaryCurrencySymbolOrName: GetPrimaryCurrencySymbolOrName,
    private val getSecondaryCurrencySymbol: GetSecondaryCurrencySymbol,
    private val fetchRekeyedAccounts: FetchRekeyedAccounts,
    private val fetchAccountInformationAndCacheAssets: FetchAccountInformationAndCacheAssets,
    private val getAssetName: GetAssetName,
    private val getAccountDisplayName: GetAccountDisplayName,
    private val getAssetDrawableProvider: GetAssetDrawableProvider,
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
    ) = flow {
        fetchAccountInformationAndCacheAssets(accountAddress).onSuccess { accountInformation ->
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
            val copiedPreview = preview.copy(
                isLoading = false,
                foundAccountInformationItemList = foundAccountInformationItemList
            )
            emit(copiedPreview)
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
        val algoAssetItem = getAccountBaseOwnedAssetData(accountInformation.address, ALGO_ASSET_ID)?.run {
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

    private suspend fun createAssetListItems(
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

    private suspend fun createAssetItem(
        baseAccountAssetData: BaseAccountAssetData,
        onCalculationDone: (BigDecimal, BigDecimal) -> Unit
    ): BaseFoundAccountInformationItem.AssetItem? {
        return (baseAccountAssetData as? BaseAccountAssetData.BaseOwnedAssetData)?.run {
            createAssetItem(
                assetId = id,
                name = getAssetName(name),
                shortName = getAssetName(shortName),
                verificationTierConfiguration = verificationTierConfigurationMapper(verificationTier),
                baseAssetDrawableProvider = getAssetDrawableProvider(id),
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
        return createAccountItem(
            accountDisplayName = getAccountDisplayName(accountInformation.address),
            accountIconDrawablePreview = AccountIconDrawablePreview(
                backgroundColorResId = R.color.wallet_4,
                iconTintResId = R.color.wallet_4_icon,
                iconResId = R.drawable.ic_rekey_shield
            ),
            formattedSecondaryValue = primaryAccountValue.formatAsCurrency(getPrimaryCurrencySymbolOrName()),
            formattedPrimaryValue = secondaryAccountValue.formatAsCurrency(getSecondaryCurrencySymbol()),
        )
    }

    private suspend fun crateAuthAccount(rekeyAdminAddress: String?): BaseFoundAccountInformationItem.AccountItem? {
        return rekeyAdminAddress?.mapNotBlank { safeRekeyAdminAddress ->
            createAccountItem(
                accountDisplayName = getAccountDisplayName(safeRekeyAdminAddress),
                accountIconDrawablePreview = AccountIconDrawablePreview(
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
                accountDisplayName = getAccountDisplayName(rekeyedAccountAddress),
                accountIconDrawablePreview = AccountIconDrawablePreview(
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
