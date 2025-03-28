package com.algorand.android.migration

import com.algorand.android.modules.settings.domain.usecase.GetMigratedTo6xCheck
import com.algorand.android.modules.settings.domain.usecase.MigrateTo6xUseCase
import com.algorand.android.modules.settings.domain.usecase.SaveMigratedTo6xCheck
import javax.inject.Inject

class Account6xMigrationManager @Inject constructor(
    private val saveMigratedTo6xCheck: SaveMigratedTo6xCheck,
    private val getMigratedTo6xCheck: GetMigratedTo6xCheck,
    private val migrateTo6xUseCase: MigrateTo6xUseCase
) {

    suspend fun migrateTo6xIfNeeded() {
        val migratedTo6X = getMigratedTo6xCheck.invoke()
        if (!migratedTo6X) {
            migrateTo6xUseCase.invoke()
            saveMigratedTo6xCheck.invoke(true)
        }
    }
}
