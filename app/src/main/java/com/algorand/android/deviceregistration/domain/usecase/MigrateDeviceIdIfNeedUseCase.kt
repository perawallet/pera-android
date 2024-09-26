package com.algorand.android.deviceregistration.domain.usecase

import com.algorand.android.module.deviceid.domain.usecase.GetNotificationUserId
import com.algorand.android.module.deviceid.domain.usecase.SetMainnetDeviceId
import com.algorand.android.module.deviceid.domain.usecase.SetNotificationUserId
import com.algorand.android.module.deviceid.domain.usecase.SetTestnetDeviceId
import com.algorand.android.module.node.domain.Node
import com.algorand.android.module.node.domain.usecase.GetActiveNode
import javax.inject.Inject

internal class MigrateDeviceIdIfNeedUseCase @Inject constructor(
    private val getNotificationUserId: GetNotificationUserId,
    private val setMainnetDeviceId: SetMainnetDeviceId,
    private val setTestnetDeviceId: SetTestnetDeviceId,
    private val setNotificationUserId: SetNotificationUserId,
    private val getActiveNode: GetActiveNode
) : MigrateDeviceIdIfNeed {
    override suspend fun invoke() {
        val oldDeviceId = getNotificationUserId() ?: return
        when (getActiveNode()) {
            Node.Mainnet -> migrateOldDeviceIdToMainnet(oldDeviceId)
            Node.Testnet -> migrateOldDeviceIdToTestnet(oldDeviceId)
        }
    }

    private fun migrateOldDeviceIdToMainnet(oldDeviceId: String) {
        setMainnetDeviceId(oldDeviceId)
        setNotificationUserId(null)
    }

    private fun migrateOldDeviceIdToTestnet(oldDeviceId: String) {
        setTestnetDeviceId(oldDeviceId)
        setNotificationUserId(null)
    }
}
