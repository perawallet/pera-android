package com.algorand.wallet.account.webauthn.domain.usecase

import com.algorand.wallet.account.webauthn.domain.model.Passkey
import kotlinx.coroutines.flow.Flow

fun interface GetAllPasskeysAsFlow {
    operator fun invoke(): Flow<List<Passkey>>
}
