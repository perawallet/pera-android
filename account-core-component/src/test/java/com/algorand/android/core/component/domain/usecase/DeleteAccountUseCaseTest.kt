package com.algorand.android.module.account.core.component.domain.usecase

import com.algorand.android.account.localaccount.domain.usecase.DeleteLocalAccount
import com.algorand.android.module.account.info.domain.usecase.DeleteAccountInformation
import com.algorand.android.module.account.core.component.domain.usecase.DeleteAccountUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.*

internal class DeleteAccountUseCaseTest {

    private val deleteLocalAccount: DeleteLocalAccount = mock()
    private val deleteAccountInformation: DeleteAccountInformation = mock()

    private val sut = DeleteAccountUseCase(deleteLocalAccount, deleteAccountInformation)

    @Test
    fun `EXPECT account and cached details to be deleted`() = runTest {
        sut("ADDRESS")

        verify(deleteLocalAccount).invoke("ADDRESS")
        verify(deleteAccountInformation).invoke("ADDRESS")
    }
}
