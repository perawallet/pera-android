package com.algorand.android.modules.perawebview

import com.algorand.android.modules.peraserializer.PeraSerializer
import com.algorand.android.usecase.GetLocalAccountsUseCase
import com.google.crypto.tink.subtle.Base64
import javax.inject.Inject

class GetAuthorizedAddressesWebMessagesUseCase @Inject constructor(
    private val localAccountsUseCase: GetLocalAccountsUseCase,
    private val peraSerializer: PeraSerializer,
    private val peraWebMessageBuilder: PeraWebMessageBuilder
) : GetAuthorizedAddressesWebMessage {

    override suspend fun invoke(): String {
        val addressNameMap = getAddressNameMap()
        val messagePayload = getMessagePayload(addressNameMap)
        return peraWebMessageBuilder.buildMessage(PeraWebMessageAction.GET_AUTHORIZED_ADDRESSES, messagePayload)
    }

    private fun getAddressNameMap(): List<Map<String, String>> {
        val localAccounts = localAccountsUseCase.getLocalAccountsThatCanSignTransaction()
        return localAccounts.map {
            mapOf(it.address to it.name)
        }
    }

    private fun getMessagePayload(addressNameMap: List<Map<String, String>>): String {
        return Base64.encode(peraSerializer.toJson(addressNameMap).toByteArray())
    }
}
