package com.algorand.android.core.component.domain.usecase

import com.algorand.android.account.localaccount.domain.usecase.DeleteLocalAccount
import com.algorand.android.accountinfo.component.domain.usecase.DeleteAccountInformation
import com.algorand.android.accountsorting.component.domain.usecase.RemoveAccountOrderIndex
import com.algorand.android.asb.component.domain.usecase.RemoveAccountAsbBackUpStatus
import com.algorand.android.custominfo.component.domain.usecase.DeleteCustomInfo
import javax.inject.Inject

internal class DeleteAccountUseCase @Inject constructor(
    private val deleteLocalAccount: DeleteLocalAccount,
    private val deleteAccountInformation: DeleteAccountInformation,
    private val deleteAccountAsbBackUpStatus: RemoveAccountAsbBackUpStatus,
    private val deleteAccountOrderIndex: RemoveAccountOrderIndex,
    private val deleteCustomInfo: DeleteCustomInfo
) : DeleteAccount {

    override suspend fun invoke(address: String) {
        deleteLocalAccount(address)
        deleteAccountInformation(address)
        deleteAccountAsbBackUpStatus(address)
        deleteAccountOrderIndex(address)
        deleteCustomInfo(address)
        // custom info still has account
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
