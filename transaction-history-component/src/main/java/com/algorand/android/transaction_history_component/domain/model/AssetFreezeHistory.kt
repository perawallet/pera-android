package com.algorand.android.transaction_history_component.domain.model

data class AssetFreezeHistory(
    val receiverAddress: String?,
    val assetId: Long?,
    val newFreezeStatus: Boolean?
)
