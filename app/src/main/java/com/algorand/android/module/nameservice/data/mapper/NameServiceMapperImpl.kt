package com.algorand.android.module.nameservice.data.mapper

import com.algorand.android.module.nameservice.data.model.NameServicePayload
import com.algorand.android.module.nameservice.domain.model.NameService
import javax.inject.Inject

internal class NameServiceMapperImpl @Inject constructor(
    private val nameServiceSourceMapper: NameServiceSourceMapper
) : NameServiceMapper {

    override fun invoke(responses: List<NameServicePayload>): List<NameService> {
        return responses.mapNotNull {
            NameService(
                accountAddress = it.address ?: return@mapNotNull null,
                nameServiceName = it.nameResponse?.name,
                nameServiceSource = nameServiceSourceMapper(it.nameResponse?.source),
                nameServiceUri = it.nameResponse?.imageUri
            )
        }
    }
}
