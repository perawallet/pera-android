package com.algorand.android.core.component.domain.usecase

import com.algorand.android.core.utils.toShortenedAddress
import com.algorand.android.custominfo.component.domain.usecase.SetCustomName
import javax.inject.Inject

internal class UpdateAccountNameUseCase @Inject constructor(
    private val setCustomName: SetCustomName
) : UpdateAccountName {

    override suspend fun invoke(address: String, name: String?) {
        val safeName = if (name.isNullOrBlank()) address.toShortenedAddress() else name
        setCustomName(address, safeName)
    }
}
