package com.algorand.wallet.analytics.domain.usecase

import com.algorand.wallet.analytics.domain.repository.ReferrerRepository
import com.algorand.wallet.analytics.domain.usecases.model.ReferrerData
import javax.inject.Inject

internal class SaveReferrerDataUseCase @Inject constructor(
    private val referrerRepository: ReferrerRepository
) : SaveReferrerData {

    override suspend fun invoke(referrerData: ReferrerData) {
        return referrerRepository.saveReferrerData(referrerData)
    }
}
