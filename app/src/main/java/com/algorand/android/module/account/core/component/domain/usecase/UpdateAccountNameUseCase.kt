package com.algorand.android.module.account.core.component.domain.usecase

import com.algorand.android.module.account.core.component.utils.toShortenedAddress
import com.algorand.android.module.custominfo.domain.usecase.SetCustomName
import javax.inject.Inject

internal class UpdateAccountNameUseCase @Inject constructor(
    private val setCustomName: SetCustomName
) : UpdateAccountName {

    override suspend fun invoke(address: String, name: String?) {
        val safeName = if (name.isNullOrBlank()) address.toShortenedAddress() else name
        setCustomName(address, safeName)
    }
}
