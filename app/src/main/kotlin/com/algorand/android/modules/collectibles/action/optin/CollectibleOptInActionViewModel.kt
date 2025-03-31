package com.algorand.android.modules.collectibles.action.optin

import androidx.lifecycle.SavedStateHandle
import com.algorand.android.models.AssetAction
import com.algorand.android.modules.assets.action.base.BaseAssetActionViewModel
import com.algorand.android.modules.verificationtier.ui.decider.VerificationTierConfigurationDecider
import com.algorand.android.usecase.AccountAddressUseCase
import com.algorand.android.usecase.GetFormattedTransactionFeeAmountUseCase
import com.algorand.android.utils.getOrThrow
import com.algorand.wallet.asset.domain.usecase.FetchAndCacheAssets
import com.algorand.wallet.asset.domain.usecase.GetAsset
import com.algorand.wallet.viewmodel.StateDelegate
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CollectibleOptInActionViewModel @Inject constructor(
    private val getFormattedTransactionFeeAmountUseCase: GetFormattedTransactionFeeAmountUseCase,
    accountAddressUseCase: AccountAddressUseCase,
    stateDelegate: StateDelegate<ViewState>,
    verificationTierConfigurationDecider: VerificationTierConfigurationDecider,
    fetchAndCacheAssets: FetchAndCacheAssets,
    getAsset: GetAsset,
    savedStateHandle: SavedStateHandle
) : BaseAssetActionViewModel(
    accountAddressUseCase,
    stateDelegate,
    verificationTierConfigurationDecider,
    fetchAndCacheAssets,
    getAsset
) {

    private val assetAction: AssetAction = savedStateHandle.getOrThrow(ASSET_ACTION_KEY)

    val accountAddress: String = assetAction.publicKey.orEmpty()
    override val assetId: Long = assetAction.assetId

    init {
        fetchAssetDescription(assetId)
    }

    fun getTransactionFee(): String {
        return getFormattedTransactionFeeAmountUseCase.getTransactionFee()
    }
}
