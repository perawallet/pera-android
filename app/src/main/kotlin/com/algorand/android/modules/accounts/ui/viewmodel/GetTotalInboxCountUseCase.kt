package com.algorand.android.modules.accounts.ui.viewmodel

import com.algorand.android.modules.accounts.ui.model.InboxButtonLabel
import com.algorand.wallet.inbox.asset.domain.usecase.GetAssetInboxRequestCountFlow
import com.algorand.wallet.inbox.domain.usecase.GetInboxMessages
import com.algorand.wallet.remoteconfig.domain.model.FeatureToggle
import com.algorand.wallet.remoteconfig.domain.usecase.IsFeatureToggleEnabled
import kotlinx.coroutines.flow.first
import javax.inject.Inject

internal class GetTotalInboxCountUseCase @Inject constructor(
    private val getAssetInboxRequestCountFlow: GetAssetInboxRequestCountFlow,
    private val getInboxMessages: GetInboxMessages,
    private val isFeatureToggleEnabled: IsFeatureToggleEnabled
) : GetTotalInboxCount {

    override suspend fun invoke(): InboxButtonLabel? {
        val isJointEnabled = isFeatureToggleEnabled(FeatureToggle.JOINT_ACCOUNT.key)

        if (isJointEnabled) {
            val inboxMessages = getInboxMessages()
            if (!inboxMessages?.jointAccountImportRequests.isNullOrEmpty()) {
                return InboxButtonLabel.JointAccountRequest
            }
            if (!inboxMessages?.jointAccountSignRequests.isNullOrEmpty()) {
                return InboxButtonLabel.SignTxnRequest
            }
        }

        val asaCount = getAssetInboxRequestCountFlow().first()
        if (asaCount > 0) return InboxButtonLabel.Inbox
        return null
    }
}
