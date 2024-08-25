package com.algorand.android.asb.component.mnemonics.domain.repository

internal interface AsbMnemonicsRepository {

    suspend fun storeBackupMnemonics(mnemonics: String)

    suspend fun getBackupMnemonics(): String?

    suspend fun clearBackupMnemonics()
}
