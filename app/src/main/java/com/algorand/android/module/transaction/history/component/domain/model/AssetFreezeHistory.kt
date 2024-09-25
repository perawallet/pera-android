package com.algorand.android.module.transaction.history.component.domain.model

data class AssetFreezeHistory(
    val receiverAddress: String?,
    val assetId: Long?,
    val newFreezeStatus: Boolean?
)
