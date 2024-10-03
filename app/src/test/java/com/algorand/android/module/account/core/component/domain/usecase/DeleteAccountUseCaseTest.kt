package com.algorand.android.module.account.core.component.domain.usecase

import com.algorand.android.module.account.info.domain.usecase.DeleteAccountInformation
import com.algorand.android.module.account.local.domain.usecase.DeleteLocalAccount
import com.algorand.android.module.account.sorting.domain.usecase.RemoveAccountOrderIndex
import com.algorand.android.module.asb.domain.usecase.RemoveAccountAsbBackUpStatus
import com.algorand.android.module.custominfo.domain.usecase.DeleteCustomInfo
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

internal class DeleteAccountUseCaseTest {

    private val deleteLocalAccount: DeleteLocalAccount = mock()
    private val deleteAccountInformation: DeleteAccountInformation = mock()
    private val deleteAccountAsbBackUpStatus: RemoveAccountAsbBackUpStatus = mock()
    private val deleteAccountOrderIndex: RemoveAccountOrderIndex = mock()
    private val deleteCustomInfo: DeleteCustomInfo = mock()

    private val sut = DeleteAccountUseCase(
        deleteLocalAccount,
        deleteAccountInformation,
        deleteAccountAsbBackUpStatus,
        deleteAccountOrderIndex,
        deleteCustomInfo
    )

    @Test
    fun `EXPECT account and cached details to be deleted`() = runTest {
        sut("ADDRESS")

        verify(deleteLocalAccount).invoke("ADDRESS")
        verify(deleteAccountInformation).invoke("ADDRESS")
        verify(deleteAccountAsbBackUpStatus).invoke("ADDRESS")
        verify(deleteAccountOrderIndex).invoke("ADDRESS")
        verify(deleteCustomInfo).invoke("ADDRESS")
    }
}
