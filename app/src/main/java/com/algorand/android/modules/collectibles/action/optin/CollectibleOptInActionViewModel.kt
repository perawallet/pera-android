package com.algorand.android.modules.collectibles.action.optin

import androidx.lifecycle.SavedStateHandle
import com.algorand.android.assetaction.AssetActionViewModel
import com.algorand.android.assetaction.usecase.GetAssetActionPreview
import com.algorand.android.models.AssetAction
import com.algorand.android.utils.AssetName
import com.algorand.android.utils.getOrThrow
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CollectibleOptInActionViewModel @Inject constructor(
    getAssetActionPreview: GetAssetActionPreview,
    savedStateHandle: SavedStateHandle
) : AssetActionViewModel(getAssetActionPreview) {

    private val assetAction: AssetAction = savedStateHandle.getOrThrow(ASSET_ACTION_KEY)

    val accountAddress: String = assetAction.publicKey.orEmpty()
    val assetName: AssetName = AssetName.create(assetAction.assetFullName)
    override val assetId: Long = assetAction.assetId

    init {
        initPreview(accountAddress)
    }
}
