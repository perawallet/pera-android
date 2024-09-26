package com.algorand.android.module.asb.domain.usecase.implementation

import com.algorand.android.module.date.DateConstants.ISO_EXTENDED_DATE_FORMAT
import com.algorand.android.module.date.domain.usecase.GetCurrentZonedDateTime
import com.algorand.android.module.asb.domain.usecase.CreateAsbBackUpFileName
import java.time.format.DateTimeFormatter
import javax.inject.Inject

internal class CreateAsbBackUpFileNameUseCase @Inject constructor(
    private val getCurrentZonedDateTime: GetCurrentZonedDateTime
) : CreateAsbBackUpFileName {

    override fun invoke(): String {
        val backupFileNameFormatter = DateTimeFormatter.ofPattern(ISO_EXTENDED_DATE_FORMAT)
        val currentZonedDateTime = getCurrentZonedDateTime()
        return currentZonedDateTime.format(backupFileNameFormatter) + BACKUP_FILE_SUFFIX
    }

    private companion object {
        private const val BACKUP_FILE_SUFFIX = "_backup.txt"
    }
}
