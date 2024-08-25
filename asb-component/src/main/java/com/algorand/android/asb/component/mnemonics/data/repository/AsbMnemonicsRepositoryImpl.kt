package com.algorand.android.asb.component.mnemonics.data.repository

import com.algorand.android.asb.component.mnemonics.domain.repository.AsbMnemonicsRepository
import com.algorand.android.caching.SharedPrefLocalSource
import com.algorand.android.encryption.EncryptionManager
import javax.inject.Inject

internal class AsbMnemonicsRepositoryImpl @Inject constructor(
    private val encryptionManager: EncryptionManager,
    private val asbMnemonicsLocalSource: SharedPrefLocalSource<String>
) : AsbMnemonicsRepository {

    override suspend fun storeBackupMnemonics(mnemonics: String) {
        val encryptedMnemonics = encryptionManager.encrypt(mnemonics)
        asbMnemonicsLocalSource.saveData(encryptedMnemonics)
    }

    override suspend fun getBackupMnemonics(): String? {
        return asbMnemonicsLocalSource.getDataOrNull()?.let { encryptionManager.decrypt(it) }
    }

    override suspend fun clearBackupMnemonics() {
        asbMnemonicsLocalSource.clear()
    }
}
