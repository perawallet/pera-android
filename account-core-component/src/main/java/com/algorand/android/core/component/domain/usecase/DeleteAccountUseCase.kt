package com.algorand.android.core.component.domain.usecase

import com.algorand.android.account.localaccount.domain.usecase.DeleteLocalAccount
import com.algorand.android.accountinfo.component.domain.usecase.DeleteAccountInformation
import com.algorand.android.accountsorting.component.domain.usecase.RemoveAccountOrderIndex
import com.algorand.android.asb.component.domain.usecase.RemoveAccountAsbBackUpStatus
import javax.inject.Inject

internal class DeleteAccountUseCase @Inject constructor(
    private val deleteLocalAccount: DeleteLocalAccount,
    private val deleteAccountInformation: DeleteAccountInformation,
    private val removeAccountAsbBackUpStatus: RemoveAccountAsbBackUpStatus,
    private val removeAccountOrderIndex: RemoveAccountOrderIndex
) : DeleteAccount {

    override suspend fun invoke(address: String) {
        deleteLocalAccount(address)
        deleteAccountInformation(address)
        removeAccountAsbBackUpStatus(address)
        removeAccountOrderIndex(address)
        /*
        TODO
        walletConnectManager.killAllSessionsByPublicKey(publicKey)
        accountManager.removeAccount(publicKey)
        notificationRepository.deleteFilterFromDatabase(publicKey)
        accountCacheManager.removeCacheData(publicKey)
        accountRepository.removeCachedAccount(publicKey)
         */
    }
}
