package com.algorand.android.module.asb.backupprotocol.usecase.implementation

import android.util.Base64
import com.algorand.android.module.asb.backupprotocol.model.BackUpAccount
import com.algorand.android.module.asb.backupprotocol.model.BackUpProtocolElement
import com.algorand.android.module.asb.backupprotocol.model.BackUpProtocolPayload
import com.algorand.android.module.asb.backupprotocol.usecase.CreateBackUpProtocolPayload
import javax.inject.Inject

internal class CreateBackUpProtocolPayloadUseCase @Inject constructor() : CreateBackUpProtocolPayload {

    override fun invoke(nodeDeviceId: String, accounts: List<BackUpAccount>): BackUpProtocolPayload {
        return BackUpProtocolPayload(
            deviceId = nodeDeviceId,
            providerName = DEFAULT_PROVIDER_NAME,
            accounts = getAccountBackUpProtocolElements(accounts)
        )
    }

    private fun getAccountBackUpProtocolElements(accounts: List<BackUpAccount>): List<BackUpProtocolElement> {
        return accounts.map {
            BackUpProtocolElement(
                address = it.address,
                name = it.name,
                accountType = getAccountType(),
                privateKey = it.secretKey.encodeBase64(),
                metadata = null
            )
        }
    }

    // TODO: we need to add other account types as well whenever we started to support them on
    //  Algorand Secure Backup, Web import and Web Export feature
    private fun getAccountType(): String {
        return "single"
    }

    private fun ByteArray.encodeBase64(): String? {
        return try {
            Base64.encodeToString(this, Base64.NO_WRAP)
        } catch (exception: Exception) {
            null
        }
    }

    private companion object {
        const val DEFAULT_PROVIDER_NAME = "Pera Wallet"
    }
}
