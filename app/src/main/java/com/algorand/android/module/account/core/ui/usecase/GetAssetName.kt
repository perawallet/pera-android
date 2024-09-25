package com.algorand.android.module.account.core.ui.usecase

import com.algorand.android.module.account.core.ui.model.AssetName

interface GetAssetName {
    operator fun invoke(name: String?): AssetName
}
