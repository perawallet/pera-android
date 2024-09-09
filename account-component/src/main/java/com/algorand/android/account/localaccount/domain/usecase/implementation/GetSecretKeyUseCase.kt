package com.algorand.android.account.localaccount.domain.usecase.implementation

import com.algorand.android.account.localaccount.domain.repository.Algo25AccountRepository
import com.algorand.android.account.localaccount.domain.usecase.GetSecretKey
import javax.inject.Inject

internal class GetSecretKeyUseCase @Inject constructor(
    private val algo25AccountRepository: Algo25AccountRepository
) : GetSecretKey {

    override suspend fun invoke(address: String): ByteArray? {
        return algo25AccountRepository.getAccount(address)?.secretKey
    }
}
