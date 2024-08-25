package com.algorand.android.deviceregistration.domain.usecase

// TODO Move this use case into deviceid omcponent after refactoring node package
interface MigrateDeviceIdIfNeed {
    suspend operator fun invoke()
}
