package com.algorand.android.module.account.core.component.domain.usecase

import com.algorand.android.account.localaccount.domain.usecase.DeleteLocalAccount
import com.algorand.android.module.account.info.domain.usecase.DeleteAccountInformation
import com.algorand.android.module.account.sorting.domain.usecase.RemoveAccountOrderIndex
import com.algorand.android.module.asb.domain.usecase.RemoveAccountAsbBackUpStatus
import com.algorand.android.module.custominfo.domain.usecase.DeleteCustomInfo
import javax.inject.Inject

internal class DeleteAccountUseCase @Inject constructor(
    private val deleteLocalAccount: DeleteLocalAccount,
    private val deleteAccountInformation: DeleteAccountInformation,
    private val deleteAccountAsbBackUpStatus: RemoveAccountAsbBackUpStatus,
    private val deleteAccountOrderIndex: RemoveAccountOrderIndex,
    private val deleteCustomInfo: DeleteCustomInfo
) : DeleteAccount {

    override suspend fun invoke(address: String) {
        deleteAccountAsbBackUpStatus(address)
        deleteAccountOrderIndex(address)
        deleteCustomInfo(address)
        deleteAccountInformation(address)
        deleteLocalAccount(address)
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
