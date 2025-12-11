/*
 * Copyright 2022-2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.modules.webimport.loading.domain.usecase

import app.perawallet.gomobilesdk.sdk.Sdk
import com.algorand.android.models.AccountCreation
import com.algorand.android.models.Result
import com.algorand.android.modules.webimport.loading.data.model.BackupTransferAccountElement
import com.algorand.android.modules.webimport.loading.domain.model.ImportedAccountResult
import com.algorand.android.modules.webimport.loading.domain.repository.WebImportAccountRepository
import com.algorand.android.usecase.AccountAdditionUseCase
import com.algorand.android.utils.DataResource
import com.algorand.android.utils.analytics.CreationType
import com.algorand.android.utils.decodeBase64OrByteArray
import com.algorand.android.utils.decrypt
import com.algorand.android.utils.exceptions.DecryptionException
import com.algorand.android.utils.exceptions.EmptyContentException
import com.algorand.android.utils.fromJson
import com.algorand.android.utils.toShortenedAddress
import com.algorand.wallet.account.detail.domain.model.AccountRegistrationType
import com.algorand.wallet.account.detail.domain.usecase.GetAccountDetail
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Named

class WebImportAccountDecryptionUseCase @Inject constructor(
    private val gson: Gson,
    private val accountAdditionUseCase: AccountAdditionUseCase,
    private val getAccountDetail: GetAccountDetail,
    @param:Named(WebImportAccountRepository.REPOSITORY_INJECTION_NAME)
    private val webImportAccountRepository: WebImportAccountRepository
) {

    fun importEncryptedBackup(
        backupId: String,
        encryptionKey: String
    ): Flow<DataResource<ImportedAccountResult>> = flow {
        emit(DataResource.Loading())
        webImportAccountRepository
            .importEncryptedBackup(backupId).use(
                onSuccess = { importBackupResponse ->
                    if (importBackupResponse.encryptedContent != null) {
                        val result = createWebImportDecryptedContent(
                            importBackupResponse.encryptedContent,
                            encryptionKey
                        )
                        emit(handleDecryptionResult(result))
                    } else {
                        emit(DataResource.Error.Local(EmptyContentException()))
                    }
                },
                onFailed = { exception, code ->
                    emit(DataResource.Error.Api(exception, code))
                }
            )
    }

    private fun createWebImportDecryptedContent(
        content: String,
        encryptionKey: String
    ): Result<String> {

        val keyBytes = encryptionKey.decodeBase64OrByteArray()
            ?: return Result.Error(DecryptionException())

        val decrypted = content.decodeBase64OrByteArray()
            ?.decrypt(secretKey = keyBytes)
            ?: return Result.Error(DecryptionException())

        return if (decrypted.errorCode == 0L) {
            Result.Success(String(decrypted.decryptedData))
        } else {
            Result.Error(DecryptionException(decrypted.errorCode))
        }
    }

    private suspend fun handleDecryptionResult(result: Result<String>): DataResource<ImportedAccountResult> {
        when (result) {
            is Result.Success -> {
                val elements = gson.fromJson<List<BackupTransferAccountElement>>(result.data)
                val importedAccounts = mutableListOf<String>()
                val unimportedAccounts = mutableListOf<String>()
                elements?.forEach {
                    val privateKey = it.privateKey?.decodeBase64OrByteArray()
                    val publicKey = Sdk.generateAddressFromSK(privateKey)
                    if (shouldSkipImport(publicKey) || privateKey == null) {
                        unimportedAccounts.add(publicKey)
                    } else {
                        val recoveredAccount = AccountCreation(
                            address = publicKey,
                            customName = it.name ?: publicKey.toShortenedAddress(),
                            isBackedUp = true,
                            type = AccountCreation.Type.Algo25(privateKey),
                            creationType = CreationType.RECOVER
                        )
                        accountAdditionUseCase.addNewAccount(recoveredAccount)
                        importedAccounts.add(publicKey)
                    }
                }
                return DataResource.Success(ImportedAccountResult(importedAccounts, unimportedAccounts))
            }

            is Result.Error -> {
                return DataResource.Error.Local(result.exception)
            }
        }
    }

    private suspend fun shouldSkipImport(publicKey: String): Boolean {
        val sameAccount = getAccountDetail(publicKey)
        return sameAccount.accountRegistrationType != AccountRegistrationType.NoAuth
    }
}
