package com.algorand.wallet.account.webauthn.data.mapper.model

import com.algorand.wallet.account.webauthn.data.database.model.PasskeyEntity
import com.algorand.wallet.account.webauthn.domain.model.Passkey

class PasskeyMapperImpl : PasskeyMapper {
    override fun invoke(passkey: PasskeyEntity): Passkey {
        return Passkey(
            seedId = passkey.seedId,
            siteId = passkey.siteId,
            uid = passkey.userId,
            username = passkey.username,
            userHandle = passkey.userHandle,
            displayName = passkey.userHandle,
            count = passkey.count,
            lastUsed = passkey.lastUsedTimeMs,
            credId = passkey.credentialId,
            origin = null,
        )
    }
}