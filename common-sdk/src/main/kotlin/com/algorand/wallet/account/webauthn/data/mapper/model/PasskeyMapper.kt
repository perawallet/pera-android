package com.algorand.wallet.account.webauthn.data.mapper.model

import com.algorand.wallet.account.webauthn.data.database.model.PasskeyEntity
import com.algorand.wallet.account.webauthn.domain.model.Passkey

internal interface PasskeyMapper {
    operator fun invoke(passkey: PasskeyEntity): Passkey
}