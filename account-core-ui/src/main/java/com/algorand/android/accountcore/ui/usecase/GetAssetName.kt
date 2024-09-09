package com.algorand.android.accountcore.ui.usecase

import com.algorand.android.accountcore.ui.model.AssetName

interface GetAssetName {
    operator fun invoke(name: String?): AssetName
}
