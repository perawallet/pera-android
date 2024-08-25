package com.algorand.android.asb.component.domain.usecase.implementation

import com.algorand.android.asb.component.domain.usecase.CreateAsbBackUpFileName
import com.algorand.android.date.component.DateConstants.ISO_EXTENDED_DATE_FORMAT
import com.algorand.android.date.component.domain.usecase.GetCurrentZonedDateTime
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
