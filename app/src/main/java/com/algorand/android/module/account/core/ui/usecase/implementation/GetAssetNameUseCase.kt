package com.algorand.android.module.account.core.ui.usecase.implementation

import android.content.res.Resources
import com.algorand.android.module.account.core.ui.model.AssetName
import com.algorand.android.module.account.core.ui.usecase.GetAssetName
import com.algorand.android.designsystem.R
import java.util.Locale

internal class GetAssetNameUseCase(
    private val resources: Resources
) : GetAssetName {

    override fun invoke(name: String?): AssetName {
        val safeName = name ?: resources.getString(DEFAULT_ASSET_NAME_RES_ID)
        val safeAvatarName = getSafeAvatarName(safeName)
        return AssetName(safeName, safeAvatarName)
    }

    private fun getSafeAvatarName(name: String): String {
        val splitItem = name.trim().split(" ", "-").filter { it.isNotBlank() }
        return if (splitItem.size == 1) {
            splitItem.firstOrNull()
        } else {
            splitItem.joinToString("") { s -> s.substring(0, 1) }
        }?.take(ASSET_AVATAR_MAX_LETTER_COUNT)?.uppercase(Locale.ENGLISH).orEmpty()
    }

    private companion object {
        val DEFAULT_ASSET_NAME_RES_ID = R.string.unnamed
        const val ASSET_AVATAR_MAX_LETTER_COUNT = 3
    }
}
