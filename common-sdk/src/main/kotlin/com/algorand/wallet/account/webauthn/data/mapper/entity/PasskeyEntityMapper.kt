package com.algorand.wallet.account.webauthn.data.mapper.entity

import com.algorand.wallet.account.webauthn.data.database.model.PasskeyEntity
import com.algorand.wallet.account.webauthn.domain.model.Passkey

internal interface PasskeyEntityMapper {
    operator fun invoke(passkey: Passkey): PasskeyEntity
}